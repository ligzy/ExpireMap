package com.expiremap.interfaces;

import com.expiremap.impl.ExpireMapEntry;

public interface ExpireMapScheduler<K, V> {
	
	public long upcomingExpireTime();

	public void addEntryToQueueForExpiration(ExpireMapEntry<K, V> e);
	
	public void removeEntryFromScheduledExpiration(ExpireMapEntry<K, V> e);
	
	public void removeAllExpiredEntries();
	
	public void terminateThread();
}
