package com.expiremap.impl;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.expiremap.interfaces.ExpireMap;
import com.expiremap.interfaces.ExpireMapScheduler;

public class CustomExpireMap<K, V> implements ExpireMap<K, V> {
	
	private final ConcurrentMap<K, ExpireMapEntry<K,V>> baseMap;

    private final ExpireMapScheduler<K, V> scheduler;
    
    //default constructor used a basic scheduler
    //for now using a ConcurrentHashMap as default
    public CustomExpireMap() {
    	super();
    	this.scheduler = new BasicScheduler<>();
    	this.baseMap = new ConcurrentHashMap<K, ExpireMapEntry<K,V>>();
	}
    
    // constructor for providing custom scheduler
    // custom schedulers can also in turn have custom Queues/data structure that can be used
    public CustomExpireMap(ExpireMapScheduler<K, V> customScheduler) {
    	super();
    	if (customScheduler == null) {
            throw new NullPointerException("Scheduler provided is null");
        }
    	// TODO make HashMap also dynamic instead of hard-coding a ConcurrentHashMap
    	// Like the scheduler
    	this.scheduler = customScheduler;
    	this.baseMap = new ConcurrentHashMap<K, ExpireMapEntry<K,V>>();
	}
    
    @Override
	public V get(Object key) {
		ExpireMapEntry<K, V> eme = this.baseMap.get(key);
        return ((eme == null) || removeExpired(eme)) ? null : eme.getValue();
	}
    
    @Override
	public V put(K key, V value, long timeoutMs) {
    	ExpireMapEntry<K, V> eme = new ExpireMapEntry<K, V>(this, key, value, timeoutMs);
    	ExpireMapEntry<K, V> oe = this.baseMap.put(key, eme);
        
    	if (oe != null) {
        	removeEntryFromScheduledExpiration(oe);
        }

        addEntryToQueueForExpiration(eme);
        return ((oe == null) || oe.isExpireTimeUp()) ? null : oe.getValue();		
	}
    
	@Override
	public V remove(Object key) {
		ExpireMapEntry<K, V> eme = this.baseMap.remove(key);
        if (eme != null) {
        	removeEntryFromScheduledExpiration(eme);
        }
        return ((eme == null) || eme.isExpireTimeUp()) ? null : eme.getValue();
	}
	
	boolean removeExpired(ExpireMapEntry<K, V> eme) {
		if(eme.isExpireTimeUp()) {
			this.baseMap.remove(eme.getKey(), eme);
			removeEntryFromScheduledExpiration(eme); 
	        return true;
		}
        return false;
    }
	
	private void addEntryToQueueForExpiration(ExpireMapEntry<K, V> e) {
        this.scheduler.addEntryToQueueForExpiration(e);
    }
	
	private void removeEntryFromScheduledExpiration(ExpireMapEntry<K, V> eme) {
        this.scheduler.removeEntryFromScheduledExpiration(eme);
    }
	
	/*
	 * 
	 * Methods below are implemented as a part of inheritance convention
	 * Not asked in the coding assignment
	 * So, some of them just return a NullPointerException
	 * 
	 */
	
	@Override
	public int size() {
		return this.baseMap.size();
	}

	@Override
	public boolean isEmpty() {
		return (size() == 0);
	}

	@Override
	public boolean containsKey(Object key) {
		ExpireMapEntry<K, V> eme = this.baseMap.get(key);
        return ((eme == null) || removeExpired(eme)) ? false : true;
	}

	@Override
	public boolean containsValue(Object value) {
		if (value == null) {
            throw new NullPointerException("Value cannot be null");
        }

        for (ExpireMapEntry<K, V> e : baseMap.values()) {
            if (e.getValue().equals(value)) {
                if (removeExpired(e)) {
                    continue;
                }

                return true;
            }
        }

        return false;
	}
	
	@Override
	public Set<K> keySet() {
		throw new NullPointerException("Not implemented yet");
	}

	@Override
	public Collection<V> values() {
		throw new NullPointerException("Not implemented yet");
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		throw new NullPointerException("Not implemented yet");
	}
	
	@Override
	public V put(K key, V value) {
		throw new NullPointerException("Not implemented yet");
	}
	
	@Override
	public boolean remove(Object key, Object value) {
		throw new NullPointerException("Not implemented yet");
	}
	
	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		throw new NullPointerException("Not implemented yet");
	}

	@Override
	public V putIfAbsent(K key, V value) {
		throw new NullPointerException("Not implemented yet");
	}
	
	public V putIfAbsent(K key, V value, long timeoutMs) {
		throw new NullPointerException("Not implemented yet");
	}

	@Override
	public boolean replace(K key, V oldValue, V newValue) {
		throw new NullPointerException("Not implemented yet");
	}

	@Override
	public V replace(K key, V value) {
		throw new NullPointerException("Not implemented yet");
	}

	@Override
	public void clear() {
		throw new NullPointerException("Not implemented yet");
		
	}
	
}
