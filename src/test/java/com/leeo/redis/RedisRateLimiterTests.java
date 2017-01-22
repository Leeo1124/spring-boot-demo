package com.leeo.redis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.leeo.lock.RedisService;
import com.leeo.ratelimit.RedisRateLimiter;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisRateLimiterTests {

	@Autowired
	private RedisService redisService;

	@Test
	public void test() throws InterruptedException {
		RedisRateLimiter semaphore = new RedisRateLimiter(2, "task", redisService);

		Thread semaphore1 = new Thread(new RateLimiterThread(semaphore));
		Thread semaphore2 = new Thread(new RateLimiterThread(semaphore));
		Thread semaphore3 = new Thread(new RateLimiterThread(semaphore));

		semaphore1.start();
		semaphore2.start();
		semaphore3.start();

		semaphore1.join();
		semaphore2.join();
		semaphore3.join();
	}

	class RateLimiterThread implements Runnable {

		private RedisRateLimiter redisRateLimiter;

		public RateLimiterThread(RedisRateLimiter redisRateLimiter) {
			this.redisRateLimiter = redisRateLimiter;
		}

		@Override
		public void run() {
			if (redisRateLimiter.tryAcquire()) {
				try {
					Thread.sleep(2000);
					System.out.println("----:excute");
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					// redisRateLimiter.release();
				}
			} else {
				System.out.println("请求超出限制......");
			}
		}

	}
}
