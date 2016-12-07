package com.leeo.lock;
import java.math.BigInteger;
import java.util.concurrent.TimeUnit;

/**
 * 模拟ID生成 
 *
 */
public class IDGenerator {

	private static BigInteger id = BigInteger.valueOf(0);

	private final Lock lock;

	private static final BigInteger INCREMENT = BigInteger.valueOf(1);

	public IDGenerator(Lock lock) {
		this.lock = lock;
	}
	
	public String getAndIncrement() {
		if (lock.tryLock(3, TimeUnit.SECONDS)) {
			try {
				// TODO 这里获取到锁, 访问临界区资源
				return getAndIncrement0();
			} finally {
				lock.unlock();
			}
		}
		return null;
		//return getAndIncrement0;
	}
	
	private String getAndIncrement0() {
		String s = id.toString();
		id = id.add(INCREMENT);
		return s;
	}
}