package com.expiremap.impl;

import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

import com.expiremap.interfaces.ExpireMapQueue;

public class BasicQueue<K, V> implements ExpireMapQueue<K, V> {
	
	private ConcurrentNavigableMap<Long, ExpireMapEntry<K, V>> basicQueue;
	
	public BasicQueue() {
		basicQueue = new ConcurrentSkipListMap<Long, ExpireMapEntry<K, V>>();
	}
	
	//custom queue can be provided
	public BasicQueue(ConcurrentNavigableMap<Long, ExpireMapEntry<K, V>> queue) {
        if (queue == null) {
            throw new NullPointerException("Queue prvided is null");
        }

        this.basicQueue = queue;
    }
	
	@Override
	public long getUpcomingExpireTime() {
		return (!basicQueue.isEmpty()) ? basicQueue.firstKey() : 0;
	}

	@Override
	public Long firstKey() {
		return (!basicQueue.isEmpty()) ? basicQueue.firstKey() : (long) 0;
	}
	
	@Override
	public void put(ExpireMapEntry<K, V> e) {
		basicQueue.put(e.getExpireTime(), e);
		
	}

	@Override
	public void remove(ExpireMapEntry<K, V> e) {
		basicQueue.remove(e.getExpireTime());		
	}

	@Override
	public void removeAllExpired() {
		ConcurrentNavigableMap<Long, ExpireMapEntry<K, V>> head = basicQueue.headMap(System.nanoTime());
        if (!head.isEmpty()) {
        	for (ExpireMapEntry<K, V> e : head.values()) {
                e.removeEntryFromMap();
            }
            head.clear();
        }
		
	}

}
