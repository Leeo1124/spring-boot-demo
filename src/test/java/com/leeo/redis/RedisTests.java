package com.leeo.redis;

import java.util.Collections;
import java.util.Date;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands.SetOption;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.test.context.junit4.SpringRunner;

import redis.clients.jedis.Protocol;
import redis.clients.util.SafeEncoder;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTests {

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	
	@Resource(name = "ratelimiter")
    RedisScript<Boolean> ratelimiter;

	@Test
	public void string() {
		String taskLock = (String) this.redisTemplate.opsForValue().get("taskLock");
		System.out.println("----:" + taskLock);
		if (StringUtils.isBlank(taskLock)) {
			this.redisTemplate.opsForValue().set("taskLock", "true");
		}
	}

	@Test
	public void hash_set() {
		String data = (String) this.redisTemplate.opsForHash().get("task.hash", "date");
		System.out.println("----:" + data);
		if (StringUtils.isBlank(data)) {
			this.redisTemplate.opsForHash().put("task.hash", "date", new Date());
		}
	}
	
	@Test
	public void hash_del() {
		long data = this.redisTemplate.opsForHash().delete("task.hash", "date");
		System.out.println("----:" + data);
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
	
	public long limitCall(String k, String v) {
		return this.redisTemplate.execute(new RedisCallback<Long>() {
			public Long doInRedis(RedisConnection connection) {
				long result = 0;
				byte[] key = redisTemplate.getStringSerializer().serialize(k);
				long current = connection.lLen(key);
				if (current >= 10) {
					System.out.println("too many requests per second");
				} else {
					byte[] value = redisTemplate.getStringSerializer().serialize(v);
					if (!connection.exists(key)) {
//						connection.multi();
						result = connection.rPush(key, value);
//						connection.exec();
					} else {
						result = connection.rPushX(key, value);
					}
				}
				
				return result;
			}
		});
	}
	
	@Test
	public void lPop() {
		final String k = "getTask";
		String result = this.redisTemplate.execute(new RedisCallback<String>() {
			public String doInRedis(RedisConnection connection) {
				byte[] key = redisTemplate.getStringSerializer().serialize(k);
				return redisTemplate.getStringSerializer().deserialize(connection.lPop(key));
			}
		});
		System.out.println("----:"+result);//list为空时，返回null
	}
	
	@Test
	public void executeRedisScript() {
//		System.out.println("----:"+this.redisTemplate.opsForList().range("task", 0, 20).toString());
//		this.redisTemplate.opsForList().leftPush("task2", 2);
		
		//value默认采用 JdkSerializationRedisSerializer值会变成这种形式：\xac\xed\x00\x05t\x00\x03hcy
//		boolean result = this.redisTemplate.execute(ratelimiter, Collections.singletonList("task"), "hcy", 2);

		boolean result = this.redisTemplate.execute(ratelimiter, this.redisTemplate.getStringSerializer(), null, Collections.singletonList("task"), "hcy", "2");
		System.out.println("----:"+result);
	}
	
	@Test
	public void executeRedisScript2() {
		DefaultRedisScript<Boolean> script = new DefaultRedisScript<Boolean>();
        script.setScriptSource(new ResourceScriptSource(new ClassPathResource("lua/ratelimiter2.lua")));
        script.setResultType(Boolean.class);
        System.out.println("script:" + script.getScriptAsString());
        
		boolean result = this.redisTemplate.execute(script, this.redisTemplate.getStringSerializer(), null, Collections.singletonList("task2"), "2", "60");
		System.out.println("----:"+result);
	}
	
	@Test
	public void executeRedisScript3() {
		final String luaScript = "local result = 0;"
				+ "local key, value, limit  = KEYS[1], ARGV[1], ARGV[2];"
				+ "local current = redis.call('LLEN', key);"
				+ "if tonumber(current) >= tonumber(limit) then"
				+ "	return false;"
				+ "else"
				+ "	if redis.call('EXISTS', KEYS[1]) == 0 then"
				+ "			result = redis.call('RPUSH', key, value);"
				+ "	else"
				+ "		result = redis.call('RPUSHX', key, value);"
				+ "	end;"
				+ "	if result > 0 then"
				+ "		return true;"
				+ "	else"
				+ "		return false;"
				+ "	end;"
				+ "end";
		RedisScript<Boolean> script = new DefaultRedisScript<Boolean>(luaScript, Boolean.class);
		System.out.println("sha1:" + script.getSha1());
        System.out.println("Lua:" + script.getScriptAsString());
        
		boolean result = this.redisTemplate.execute(script, this.redisTemplate.getStringSerializer(), null, Collections.singletonList("task"), "hcy", "2");
		System.out.println("----:"+result);
	}
	
	@Test
	public void testLimitCall() {
		final String key = "getTask";
		final String[] values = {"hcy","a","b","c","d","e","f","g","h","i","j"};
		int i = 0;
		for(String value : values){
			i++;
			long result = this.limitCall(key, value);
			System.out.println(i+"--"+value+"--:" + result);
			if(result > 0){
				System.out.println("获取任务......");
			}else{
				System.out.println("请求超出限制......");
			}
		}
	}
	
}
