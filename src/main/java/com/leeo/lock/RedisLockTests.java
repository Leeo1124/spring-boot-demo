package com.leeo.lock;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisLockTests {
	
	private static Set<String> generatedIds = new HashSet<>();
	
	private static final String LOCK_KEY = "lock.task";
	private static final long LOCK_EXPIRE = 3;
	
	@Autowired
	private RedisService redisService;
	
	@Test
	public void test() throws InterruptedException {
		Lock lock1 = new RedisLock(redisService, LOCK_KEY, LOCK_EXPIRE);
		RedisIdGenerator g1 = new RedisIdGenerator(lock1, redisService);
		IDConsumeMission consume1 = new IDConsumeMission(g1, "consume1");
		
		Lock lock2 = new RedisLock(redisService, LOCK_KEY, LOCK_EXPIRE);
		RedisIdGenerator g2 = new RedisIdGenerator(lock2, redisService);
		IDConsumeMission consume2 = new IDConsumeMission(g2, "consume2");
		
		Thread t1 = new Thread(consume1);
		Thread t2 = new Thread(consume2);
		t1.start();
		t2.start();
		
//		Thread.sleep(20 * 1000); //让两个线程跑20秒
		
//		IDConsumeMission.stop();
		
		t1.join();
		t2.join();
	}
	
	@Test
	public void test2() throws InterruptedException {
		Lock lock1 = new RedisLock(redisService, LOCK_KEY, LOCK_EXPIRE);
		RedisIdGenerator g1 = new RedisIdGenerator(lock1, redisService);
		RedisIdConsumeMission consume1 = new RedisIdConsumeMission(g1, "consume1");
		
		Lock lock2 = new RedisLock(redisService, LOCK_KEY, LOCK_EXPIRE);
		RedisIdGenerator g2 = new RedisIdGenerator(lock2, redisService);
		RedisIdConsumeMission consume2 = new RedisIdConsumeMission(g2, "consume2");
		
		Thread t1 = new Thread(consume1);
		Thread t2 = new Thread(consume2);
		t1.start();
		t2.start();
		
//		Thread.sleep(20 * 1000); //让两个线程跑20秒
		
//		IDConsumeMission.stop();
		
		t1.join();
		t2.join();
	}
	
	@Test
	public void test3() throws InterruptedException {
		Lock lock1 = new RedisLock(redisService, LOCK_KEY, LOCK_EXPIRE);
		RedisIdGenerator g1 = new RedisIdGenerator(lock1, redisService);
		Producer producer = new Producer(g1, "producer");
		
		Lock lock2 = new RedisLock(redisService, LOCK_KEY, LOCK_EXPIRE);
		RedisIdGenerator g2 = new RedisIdGenerator(lock2, redisService);
		Consumer consumer = new Consumer(g2, "consumer");
		
		Thread t1 = new Thread(producer);
		Thread t2 = new Thread(consumer);
		t1.start();
		t2.start();
		
		t1.join();
		t2.join();
	}
	
	static String time() {
		return String.valueOf(System.currentTimeMillis() / 1000);
	}
	
	static class IDConsumeMission implements Runnable {

		private RedisIdGenerator generator;
		
		private String name;
		
		private static volatile boolean stop;
		
		public IDConsumeMission(RedisIdGenerator generator, String name) {
			this.generator = generator;
			this.name = name;
		}
		
		public static void stop() {
			stop = true;
		}
		
		public void run() {
			System.out.println(time() + ": consume " + name + " start ");
			while (!stop) {
				String id = generator.getAndIncrement();
				if(StringUtils.isNotBlank(id) && generatedIds.contains(id)) {
					System.out.println(time() + ": duplicate id generated, id = " + id);
					stop = true;
					continue;
				} 
				
				generatedIds.add(id);
				System.out.println(time() + ": consume " + name + " add id = " + id);
			}
			System.out.println(time() + ": consume " + name + " done ");
		}
		
	}
	
	static class RedisIdConsumeMission implements Runnable {

		private RedisIdGenerator generator;
		
		private String name;
		
		private static volatile boolean stop;
		
		public RedisIdConsumeMission(RedisIdGenerator generator, String name) {
			this.generator = generator;
			this.name = name;
		}
		
		public static void stop() {
			stop = true;
		}
		
		public void run() {
			System.out.println(time() + ": consume " + name + " start ");
			while (!stop) {
				String id = generator.getIncr();
				if(StringUtils.isNotBlank(id) && generatedIds.contains(id)) {
					System.out.println(time() + ": duplicate id generated, id = " + id);
					stop = true;
					continue;
				} 
				
				generatedIds.add(id);
				System.out.println(time() + ": consume " + name + " add id = " + id);
			}
			System.out.println(time() + ": consume " + name + " done ");
		}
		
	}
	
	static class Producer implements Runnable {

		private RedisIdGenerator generator;
		
		private String name;
		
		private static volatile boolean stop;
		
		public Producer(RedisIdGenerator generator, String name) {
			this.generator = generator;
			this.name = name;
		}
		
		public static void stop() {
			stop = true;
		}
		
		public void run() {
			System.out.println(time() + ": consume " + name + " start ");
			while (!stop) {
				String id = generator.getIncr();
				if(StringUtils.isNotBlank(id) && Long.valueOf(id) < 0) {
					System.out.println(time() + ": duplicate id generated, id = " + id);
					stop = true;
					continue;
				} 
				
				generatedIds.add(id);
				System.out.println(time() + ": consume " + name + " add id = " + id);
			}
			System.out.println(time() + ": consume " + name + " done ");
		}
		
	}
	
	static class Consumer implements Runnable {

		private RedisIdGenerator generator;
		
		private String name;
		
		private static volatile boolean stop;
		
		public Consumer(RedisIdGenerator generator, String name) {
			this.generator = generator;
			this.name = name;
		}
		
		public static void stop() {
			stop = true;
		}
		
		public void run() {
			System.out.println(time() + ": consume " + name + " start ");
			while (!stop) {
				String id = generator.getDecr();
				if(StringUtils.isNotBlank(id) && Long.valueOf(id) < 0) {
					System.out.println(time() + ": duplicate id generated, id = " + id);
					stop = true;
					continue;
				} 
				
				generatedIds.add(id);
				System.out.println(time() + ": consume " + name + " add id = " + id);
			}
			System.out.println(time() + ": consume " + name + " done ");
		}
		
	}
}

