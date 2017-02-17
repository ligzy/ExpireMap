package com.expiremap.impl;

import com.expiremap.interfaces.ExpireMapQueue;
import com.expiremap.interfaces.ExpireMapScheduler;

public class BasicScheduler<K, V> implements ExpireMapScheduler<K, V> {
	
	private final ExpireMapQueue<K, V> queue;
	
	//volatile for ~caching
	private volatile boolean hasEnded = false;

    private volatile boolean alert = false;

    private long nextTime = 0;

    private final Thread schedulerThread = new Thread(new ExpireMapSchedulerThread());

    private final Object mutex = new Object();
    
	public BasicScheduler() {
		this.queue = new BasicQueue<K, V>();
		schedulerThread.start();
	}
	
	//For providing with a custom queue-ing mechanism like priority queues, etc.
	public BasicScheduler(ExpireMapQueue<K, V> customQueue) {
		if(customQueue == null) throw new NullPointerException("Custom Queue provided is null");
		this.queue = customQueue;
		schedulerThread.start();
	}
	
	@Override
	public long upcomingExpireTime() {
		if(queue.firstKey()!=null) {
			return queue.firstKey();
		}
		return 0;
	}
	
	@Override
	public void addEntryToQueueForExpiration(ExpireMapEntry<K, V> e) {
		queue.put(e);
		if (upcomingExpireTime() != nextTime) {
            synchronized (mutex) {
                alert = true;
                mutex.notifyAll();
            }
        }
	}
	
	@Override
	public void removeEntryFromScheduledExpiration(ExpireMapEntry<K, V> e) {
		queue.remove(e);
		if (queue.firstKey() != nextTime) {
            synchronized (mutex) {
                alert = true;
                mutex.notifyAll();
            }
        }		
	}
	
	@Override
	public void removeAllExpiredEntries() {
		queue.removeAllExpired();
	}
	
	@Override
    public void terminateThread() {
        hasEnded = true;
        schedulerThread.interrupt();
        try {
        	schedulerThread.join();
        } catch (InterruptedException e) {
            // TODO
        }
    }
	
	// The scheduler thread to monitor entries
	final class ExpireMapSchedulerThread implements Runnable {

        @Override
        public void run() {
            while (!hasEnded) {
                nextTime = upcomingExpireTime();
                long timeLeft = 0;
                if(nextTime>0) {
                	timeLeft = nextTime - System.nanoTime();
                }
                while (timeLeft >= 0) {
                    if (!waiting(timeLeft) && !hasEnded) {
                        nextTime = upcomingExpireTime();
                        timeLeft = 0;
                        if(nextTime>0) {
                        	timeLeft = nextTime - System.nanoTime();
                        }                        
                    } else {
                        break;
                    }
                }
                removeAllExpiredEntries();
            }
        }

        public boolean waiting(long timeout) {
            boolean result = true;
            try {
                synchronized (mutex) {
                    alert = false;
                    mutex.wait(timeout / 1000000, (int) (timeout % 1000000));
                    result = !alert;
                }
            } catch (InterruptedException e) {
                result = false;
            }

            return result;
        }
    }

}
