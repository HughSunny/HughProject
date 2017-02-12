package com.set.util;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class CurrentMillis {
	public CurrentMillis(long currentMillis) {
		super();
		this.currentMillis = currentMillis;
		this.lock = new ReentrantReadWriteLock();
	}

	private long currentMillis;
	private ReentrantReadWriteLock lock;

	public long getCurrentMillis() {
		lock.readLock().lock();
		long retCurrentMillis = currentMillis;
		lock.readLock().unlock();
		return retCurrentMillis;
	}

	public void setCurrentMillis(long currentMillis) {
		lock.writeLock().lock();
		this.currentMillis = currentMillis;
		lock.writeLock().unlock();
		return;
	}
}
