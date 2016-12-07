package com.leeo.lock;

import java.math.BigInteger;
import java.util.concurrent.TimeUnit;

public class RedisIdGenerator {
	
	private static BigInteger id = BigInteger.valueOf(0);
	private static final BigInteger INCREMENT = BigInteger.valueOf(1);
	
	private static final String TASK_COUNT = "task.count";
	
	private final Lock lock;
	
	private RedisService redisService;

	public RedisIdGenerator(Lock lock, RedisService redisService) {
		this.lock = lock;
		this.redisService = redisService;
	}
	
	public String getAndIncrement() {
		if (lock.tryLock(3, TimeUnit.SECONDS)) {
			try {
				return getAndIncrement0();
			} finally {
				lock.unlock();
			}
		}
		return null;
	}
	
	private String getAndIncrement0() {
		String s = id.toString();
		id = id.add(INCREMENT);
		return s;
	}
	
	public String getIncr() {
		if (lock.tryLock(3, TimeUnit.SECONDS)) {
			try {
				return String.valueOf(this.redisService.incr(TASK_COUNT));
			} finally {
				lock.unlock();
			}
		}
		return null;
	}
	
	public String getDecr() {
		if (lock.tryLock(3, TimeUnit.SECONDS)) {
			try {
				return String.valueOf(this.redisService.decr(TASK_COUNT));
			} finally {
				lock.unlock();
			}
		}
		return null;
	}
}