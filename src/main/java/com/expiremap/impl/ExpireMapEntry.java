package com.expiremap.impl;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.NANOSECONDS;

import java.util.Map.Entry;

public class ExpireMapEntry<K, V> implements Entry<K, V> {
	
	private final CustomExpireMap<K, V> map;
	
	private final K key;

    private volatile V value;

    private final long ttl;

    public ExpireMapEntry(CustomExpireMap<K, V> map, K key, V value, long ttl) {
    	
    	if (ttl < 0) {
            throw new IllegalArgumentException("Time to live is less than zero");
        }
    	
    	this.map = map;
    	this.key = key;
    	this.value = value;
    	this.ttl = System.nanoTime() + NANOSECONDS.convert(ttl, MILLISECONDS);
    	
	}
	@Override
	public K getKey() {
		return this.key;
	}

	@Override
	public V getValue() {
		return this.value;
	}

	@Override
	public V setValue(V value) {
		if (value == null) {
            throw new NullPointerException("value cannot be null");
        }

        V oldVal = this.value;
        this.value = value;
        return oldVal;
	}
	
	public long getExpireTime() {
        return this.ttl;
    }

	public boolean isExpireTimeUp() {
        return System.nanoTime() > this.ttl;
    }
	
	public void removeEntryFromMap() {
		this.map.removeExpired(this);
	}

}
