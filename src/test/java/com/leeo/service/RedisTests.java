package com.leeo.service;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands.SetOption;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.test.context.junit4.SpringRunner;

import redis.clients.jedis.Protocol;
import redis.clients.util.SafeEncoder;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTests {

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@Test
	public void string() {
		String taskLock = (String) this.redisTemplate.opsForValue().get("taskLock");
		System.out.println("----:" + taskLock);
		if (StringUtils.isBlank(taskLock)) {
			this.redisTemplate.opsForValue().set("taskLock", "true");
		}
	}

	@Test
	public void hash() {
		String data = (String) this.redisTemplate.opsForHash().get("tasklock", "1");
		System.out.println("----:" + data);
		if (StringUtils.isBlank(data)) {
			this.redisTemplate.opsForHash().put("tasklock", "1", new Date());
		}
	}

	@Test
	public void set() {
		final String key = "lock.task";
		long val = System.currentTimeMillis();
		System.out.println("----:"+val);
		this.redisTemplate.execute(new RedisCallback<Object>() {
			public Object doInRedis(RedisConnection connection) {
				connection.set(
						redisTemplate.getStringSerializer().serialize(key),
						redisTemplate.getStringSerializer().serialize(String.valueOf(val)),
						Expiration.seconds(180),
						SetOption.SET_IF_ABSENT);
				return null;
			}
		});
	}
	
	@Test
	public void setNew() {
		final String key = "lock.task";
		long val = System.currentTimeMillis();
		System.out.println("----:"+val);
		final long expires = 100;
		Object result = this.redisTemplate.execute(new RedisCallback<Object>() {
			@Override
			public Object doInRedis(RedisConnection connection) {
				Object result = connection.execute(
						Protocol.Command.SET.name(),
						redisTemplate.getStringSerializer().serialize(key), 
						redisTemplate.getStringSerializer().serialize(String.valueOf(val)),
						SafeEncoder.encode("EX"),
						Protocol.toByteArray(expires),
						SafeEncoder.encode("NX"));

				return result;
			}
		});
		System.out.println("----:"+result);
	}
	
	@Test
	public void get() {
		final String k = "lock.task";
		String result = this.redisTemplate.execute(new RedisCallback<String>() {
			public String doInRedis(RedisConnection connection) {
				 byte[] key = redisTemplate.getStringSerializer().serialize(k);  
		            if (connection.exists(key)) {  
		                byte[] value = connection.get(key);  
		                String v = redisTemplate.getStringSerializer()  
		                        .deserialize(value);  

		                return v;  
		            }  
		            return null; 
			}
		});
		System.out.println("----:"+result);
	}
	
	@Test
	public void del() {  
		final String key = "lock.task";
		long count = this.redisTemplate.execute(new RedisCallback<Long>() {  
	        public Long doInRedis(RedisConnection connection) {  
	        	return connection.del(redisTemplate.getStringSerializer().serialize(key));  
	        }  
	    });  
		System.out.println("----:"+count);
	}  
	
	@Test
	public void getSet() {
		final String key = "lock.task";
		long val = System.currentTimeMillis();
		System.out.println("----:"+val);
		String result = this.redisTemplate.execute(new RedisCallback<String>() {
			public String doInRedis(RedisConnection connection) {
				byte[] value = connection.getSet(
							redisTemplate.getStringSerializer().serialize(key),
							redisTemplate.getStringSerializer().serialize(String.valueOf(val)));
				
		        return redisTemplate.getStringSerializer().deserialize(value); 
			}
		});
		System.out.println("----:"+result);
	}
	
	@Test
	public void incr() {
		final String key = "task.count";
		long result = this.redisTemplate.execute(new RedisCallback<Long>() {
			public Long doInRedis(RedisConnection connection) {
				return connection.incr(redisTemplate.getStringSerializer().serialize(key));
			}
		});
		System.out.println("----:"+result);
	}
	
	@Test
	public void decr() {
		final String key = "task.count";
		long result = this.redisTemplate.execute(new RedisCallback<Long>() {
			public Long doInRedis(RedisConnection connection) {
				return connection.decr(redisTemplate.getStringSerializer().serialize(key));
			}
		});
		System.out.println("----:"+result);
	}
	
	@Test
	public void set2() {
		final String key = "lock.task";
		long val = System.currentTimeMillis();
		System.out.println("----:"+val);
		this.redisTemplate.opsForValue().set(key, String.valueOf(val));
	}
	
	@Test
	public void get2() {
		final String key = "lock.task";
		String result = (String) this.redisTemplate.opsForValue().get(key);
		System.out.println("----:"+result);
	}
	
	@Test
	public void del2() {  
		final String key = "lock.task";
		this.redisTemplate.delete(key);
	}  
	
	@Test
	public void getSet2() {
		final String key = "lock.task";
		long val = System.currentTimeMillis();
		System.out.println("----:"+val);
		String result = (String) this.redisTemplate.opsForValue().getAndSet(key, String.valueOf(val));
		System.out.println("----:"+result);
	}
	
}
