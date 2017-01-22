package com.leeo.ratelimit;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.leeo.lock.Lock;
import com.leeo.lock.RedisLock;
import com.leeo.lock.RedisService;

public class RedisRateLimiter {

	private Logger logger = LoggerFactory.getLogger(RedisRateLimiter.class);

	private String individualPrefix = "oasis-ratelimiter:";
	private int permits;
	private String key;
	private RedisService redisService;

	public RedisRateLimiter(int permits, String key, RedisService redisService) {
		this.permits = permits;
		this.key = key;
		this.redisService = redisService;
	}
	
	public boolean tryAcquire() {
		if (Thread.interrupted()) {
			this.logger.info("当前线程已中断");
			return false;
		}
//		方式一：
//		Lock lock = new RedisLock(this.redisService, "lock.semaphore", 3);
//		if (lock.tryLock(3, TimeUnit.SECONDS)) {
//			try {
//				if (this.redisService.listPush(individualPrefix + key, Thread.currentThread().getName(), permits) > 0) {
//					return true;
//				}
//			} finally {
//				lock.unlock();
//			}
//		}
//		
//		return false;
		
//		方式二：
		return this.redisService.execute(individualPrefix + key, Thread.currentThread().getName(), permits);
	}
	
	public boolean tryAcquire(long time) {
		if (Thread.interrupted()) {
			this.logger.info("当前线程已中断");
			return false;
		}
		
		return this.redisService.execute(individualPrefix + key, Thread.currentThread().getName(), permits, time);
	}

	public void release() {
		this.redisService.lPop(individualPrefix + key);
	}
}