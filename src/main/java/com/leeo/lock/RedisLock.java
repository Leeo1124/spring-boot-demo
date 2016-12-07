package com.leeo.lock;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RedisLock implements Lock {
	
	private Logger logger = LoggerFactory.getLogger(RedisLock.class);  

	protected volatile boolean locked;

	private Thread exclusiveOwnerThread;
	
	private RedisService redisService;
	
	protected String lockKey;
	
	protected long lockExpires;
	
	public RedisLock(RedisService redisService, String lockKey, long lockExpires) {
		this.redisService = redisService;
		this.lockKey = lockKey;
		this.lockExpires = lockExpires;
	}

	public void lock() {
		try {
			lock(false, 0, null, false);
		} catch (InterruptedException e) {
			// TODO ignore
			this.logger.error("获取redis分布式锁失败", e);
		}
	}

	public void lockInterruptibly() throws InterruptedException {
		lock(false, 0, null, true);
	}
	
	public boolean tryLock() {
		boolean lockFlag = this.redisService.set(this.lockKey, this.lockExpires);
		if (lockFlag) {
			locked = true;
			setExclusiveOwnerThread(Thread.currentThread());
			return true;
		}
		
		return false;
	}

	public boolean tryLock(long time, TimeUnit unit) {
		try {
			return lock(true, time, unit, false);
		} catch (InterruptedException e) {
			// TODO ignore
			this.logger.error("获取redis分布式锁失败", e);
		}
		return false;
	}

	public boolean tryLockInterruptibly(long time, TimeUnit unit) throws InterruptedException {
		return lock(true, time, unit, true);
	}

	public boolean lock(boolean useTimeout, long time, TimeUnit unit, boolean interrupt) throws InterruptedException {
		if (interrupt) {
			checkInterruption();
		}
		
		long start = System.currentTimeMillis();
		long timeout = unit.toMillis(time); // if !useTimeout, then it's useless
		
		while (useTimeout ? isTimeout(start, timeout) : true) {
			if (interrupt) {
				checkInterruption();
			}
			boolean lockFlag = this.redisService.set(this.lockKey, this.lockExpires);
			if(lockFlag){
				locked = true;
				setExclusiveOwnerThread(Thread.currentThread());
				return true;
			}
		}
		return false;
	}
	
	public void unlock() {
		// TODO 检查当前线程是否持有锁
		if (Thread.currentThread() != getExclusiveOwnerThread()) {
			throw new IllegalMonitorStateException("current thread does not hold the lock");
		}
		
		this.redisService.delete(this.lockKey);
		
		setExclusiveOwnerThread(null);
	}
	
	private void checkInterruption() throws InterruptedException {
		if(Thread.currentThread().isInterrupted()) {
			throw new InterruptedException();
		}
	}
	
	private boolean isTimeout(long start, long timeout) {
		return start + timeout > System.currentTimeMillis();
	}

	protected void setExclusiveOwnerThread(Thread thread) {
		exclusiveOwnerThread = thread;
	}

	protected final Thread getExclusiveOwnerThread() {
		return exclusiveOwnerThread;
	}

}