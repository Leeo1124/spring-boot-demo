package com.leeo.lock;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands.SetOption;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import redis.clients.jedis.Protocol;
import redis.clients.util.SafeEncoder;

@Service
public class RedisService {

	private Logger logger = LoggerFactory.getLogger(RedisService.class);
	protected static final long EXPIRATION_TIME = 60;

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	public boolean set(final String key) {
		long value = System.currentTimeMillis();
		return this.set(key, String.valueOf(value), EXPIRATION_TIME, TimeUnit.SECONDS, SetOption.SET_IF_ABSENT);
	}

	public boolean set(final String key, final long expirationTime) {
		long value = System.currentTimeMillis();
		return this.set(key, String.valueOf(value), expirationTime, TimeUnit.SECONDS, SetOption.SET_IF_ABSENT);
	}

	public boolean set(final String key, final String value) {
		return this.set(key, value, EXPIRATION_TIME, TimeUnit.SECONDS, SetOption.SET_IF_ABSENT);
	}

	public boolean set(final String key, final String value, final long expirationTime) {
		return this.set(key, value, expirationTime, TimeUnit.SECONDS, SetOption.SET_IF_ABSENT);
	}

	public boolean set(final String key, final String value, final long expirationTime, final TimeUnit unit) {
		return this.set(key, value, expirationTime, unit);
	}

	public boolean set(final String key, final String value, final long expirationTime, final TimeUnit unit,
			SetOption option) {
		if (StringUtils.isBlank(key)) {
			throw new IllegalArgumentException("key不能为空.");
		}
		if (StringUtils.isBlank(value)) {
			throw new IllegalArgumentException("value不能为空.");
		}
		Object result = this.redisTemplate.execute(new RedisCallback<Object>() {
			@Override
			public Object doInRedis(RedisConnection connection) {
				return connection.execute(Protocol.Command.SET.name(),
						redisTemplate.getStringSerializer().serialize(key),
						redisTemplate.getStringSerializer().serialize(value),
						SafeEncoder.encode(TimeUnit.SECONDS == unit ? "EX" : "PX"),
						Protocol.toByteArray(expirationTime),
						SafeEncoder.encode(SetOption.SET_IF_ABSENT == option ? "NX" : "XX"));
			}
		});
		if (null != result) {
			this.logger.info("设置{}的值为{},超时时间为{},单位为{},SET操作为{},success.", key, value, expirationTime, unit, option);
			return true;
		}
		this.logger.info("设置{}的值为{},超时时间为{},单位为{},SET操作为{},fail.", key, value, expirationTime, unit, option);

		return false;
	}

	public boolean delete(final String key) {
		long count = this.redisTemplate.execute(new RedisCallback<Long>() {
			public Long doInRedis(RedisConnection connection) {
				return connection.del(redisTemplate.getStringSerializer().serialize(key));
			}
		});
		this.logger.info("删除{}的个数为： {}.", key, count);
		if (count == 1) {
			return true;
		}
		return false;
	}

	public String getSet(final String key) {
		long value = System.currentTimeMillis();
		return this.getSet(key, String.valueOf(value));
	}

	public String getSet(final String key, final String value) {
		String result = (String) this.redisTemplate.opsForValue().getAndSet(key, value);
		this.logger.info("{}返回值为 : {}.", key, result);

		return result;
	}

	public String get(final String key) {
		String result = this.redisTemplate.execute(new RedisCallback<String>() {
			public String doInRedis(RedisConnection connection) {
				byte[] k = redisTemplate.getStringSerializer().serialize(key);
				if (connection.exists(k)) {
					byte[] value = connection.get(k);
					String v = redisTemplate.getStringSerializer().deserialize(value);

					return v;
				}
				return null;
			}
		});
		this.logger.info("{}返回值为 : {}.", key, result);

		return result;
	}

	public long incr(final String key) {
		long result = this.redisTemplate.execute(new RedisCallback<Long>() {
			public Long doInRedis(RedisConnection connection) {
				return connection.incr(redisTemplate.getStringSerializer().serialize(key));
			}
		});
		this.logger.info("将{}中储存的数字值增一,返回值为 : {}.", key, result);

		return result;
	}

	public long decr(final String key) {
		long result = this.redisTemplate.execute(new RedisCallback<Long>() {
			public Long doInRedis(RedisConnection connection) {
				return connection.decr(redisTemplate.getStringSerializer().serialize(key));
			}
		});
		this.logger.info("将{}中储存的数字值减一,返回值为 : {}.", key, result);

		return result;
	}

}