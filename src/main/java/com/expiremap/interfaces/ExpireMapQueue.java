package com.expiremap.interfaces;

import com.expiremap.impl.ExpireMapEntry;

public interface ExpireMapQueue<K, V> {
	
	public long getUpcomingExpireTime();
	
	public Long firstKey();
	
	public void put(ExpireMapEntry<K, V> e);
	
	public void remove(ExpireMapEntry<K, V> e);
	
	public void removeAllExpired();
	
}
