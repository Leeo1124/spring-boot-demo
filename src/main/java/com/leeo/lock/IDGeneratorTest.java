package com.leeo.lock;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import redis.clients.jedis.Jedis;

public class IDGeneratorTest {
	
	private static Set<String> generatedIds = new HashSet<>();
	
	private static final String LOCK_KEY = "lock.lock";
	private static final long LOCK_EXPIRE = 5 * 1000;
	
	@Test
	public void test() throws InterruptedException {
		Jedis jedis1 = new Jedis("10.255.33.15", 6379);
		Lock lock1 = new RedisBasedDistributedLock(jedis1, LOCK_KEY, LOCK_EXPIRE);
		IDGenerator g1 = new IDGenerator(lock1);
		IDConsumeMission consume1 = new IDConsumeMission(g1, "consume1");
		
		Jedis jedis2 = new Jedis("10.255.33.15", 6379);
		Lock lock2 = new RedisBasedDistributedLock(jedis2, LOCK_KEY, LOCK_EXPIRE);
		IDGenerator g2 = new IDGenerator(lock2);
		IDConsumeMission consume2 = new IDConsumeMission(g2, "consume2");
		
		Thread t1 = new Thread(consume1);
		Thread t2 = new Thread(consume2);
		t1.start();
		t2.start();
		
		Thread.sleep(20 * 1000); //让两个线程跑20秒
		
		IDConsumeMission.stop();
		
		t1.join();
		t2.join();
	}
	
	static String time() {
		return String.valueOf(System.currentTimeMillis() / 1000);
	}
	
	static class IDConsumeMission implements Runnable {

		private IDGenerator idGenerator;
		
		private String name;
		
		private static volatile boolean stop;
		
		public IDConsumeMission(IDGenerator idGenerator, String name) {
			this.idGenerator = idGenerator;
			this.name = name;
		}
		
		public static void stop() {
			stop = true;
		}
		
		public void run() {
			System.out.println(time() + ": consume " + name + " start ");
			while (!stop) {
				String id = idGenerator.getAndIncrement();
				if(generatedIds.contains(id)) {
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