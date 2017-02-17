package com.expiremap.interfaces;

import java.util.concurrent.ConcurrentMap;

public interface ExpireMap<K, V> extends ConcurrentMap<K, V> {

	public V put(K key, V value, long timeoutMs);
	
	@Override
	public V get(Object key);
	
	@Override
	public V remove(Object key);
	
}
