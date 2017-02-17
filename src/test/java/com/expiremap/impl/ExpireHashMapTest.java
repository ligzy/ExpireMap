package com.expiremap.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.expiremap.interfaces.ExpireMap;
import com.expiremap.interfaces.ExpireMapScheduler;

public class ExpireHashMapTest {
	private ExpireMapScheduler<Integer, Integer> scheduler;
	private ExpireMap<Integer, Integer> expireMap;
	
	@Before
	public void initTest() throws Exception {
		
		scheduler = new BasicScheduler<>();
		expireMap = new CustomExpireMap<>();
	}
	
	@After
	public void endOfProgram() throws Exception {
		scheduler.terminateThread();
	}
	
	@Test
	public void putTest() {
		assertEquals(0, expireMap.size());
        assertNull(expireMap.put(0, 0, 100));
        assertEquals(1, expireMap.size());
	}
	
	@Test
	public void putTestWithExpire() throws InterruptedException {
		assertEquals(0, expireMap.size());
        expireMap.put(1, 1, 100);
        assertTrue((expireMap.size() == 1));
        Thread.sleep(200);
        assertTrue((expireMap.size() == 0));
	}
	
	@Test
	public void getTest() throws InterruptedException {
		assertEquals(0, expireMap.size());
        expireMap.put(1, 1, 100);
        assertTrue((expireMap.get(1) == 1));
	}
	
	@Test
	public void getTestWithExpire() throws InterruptedException {
		assertEquals(0, expireMap.size());
        expireMap.put(1, 1, 100);
        assertTrue((expireMap.get(1) == 1));
        Thread.sleep(200);
        assertTrue((expireMap.size() == 0));
	}
	
	@Test
	public void removeTest() throws InterruptedException {
		assertEquals(0, expireMap.size());
        expireMap.put(1, 1, 10);
        assertTrue((expireMap.size() == 1));
        expireMap.remove(1);
        assertTrue((expireMap.size() == 0));
	}
	
}
