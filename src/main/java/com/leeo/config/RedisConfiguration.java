//package com.leeo.config;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.cache.support.CompositeCacheManager;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.cache.DefaultRedisCachePrefix;
//import org.springframework.data.redis.cache.RedisCacheManager;
//import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
//import org.springframework.data.redis.serializer.StringRedisSerializer;
//
//import com.google.common.collect.Lists;
//
//import redis.clients.jedis.JedisPoolConfig;
//
///**
// * redis 配置
// *
// */
//@Configuration
//public class RedisConfiguration {
//
//	private static final Logger logger = LoggerFactory.getLogger(RedisConfiguration.class);
//
//	/**
//	 * jedis 配置 
//	 * @return
//	 */
//	public JedisPoolConfig getJedisPoolConfig(){
//		JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
//		jedisPoolConfig.setMaxTotal(200);
//		jedisPoolConfig.setMaxIdle(2);
//		jedisPoolConfig.setMinIdle(1);
//		jedisPoolConfig.setMaxWaitMillis(10000);
//		jedisPoolConfig.setTestOnBorrow(true);
//		jedisPoolConfig.setTestWhileIdle(true);
//		
//		return jedisPoolConfig;
//	}
//	
//	/**
//	 * redis服务器中心
//	 * @return
//	 */
//	public JedisConnectionFactory getJedisConnectionFactory(){
//		JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();
//		jedisConnectionFactory.setPoolConfig(getJedisPoolConfig());
//		jedisConnectionFactory.setPort(6379);
//		jedisConnectionFactory.setHostName("10.255.33.15");
//		jedisConnectionFactory.setTimeout(10000);
//		
//		return jedisConnectionFactory;
//	}
//	
//	public RedisTemplate getRedisTemplate(){
//		RedisTemplate redisTemplate = new RedisTemplate();
//		redisTemplate.setConnectionFactory(getJedisConnectionFactory());
//		redisTemplate.setKeySerializer(new StringRedisSerializer());
//		redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());
//		
//		return redisTemplate;
//	}
//	
//	/**
//	 * redisCacheManager配置
//	 * @return
//	 */
//	public RedisCacheManager getRedisCacheManager(){
//		RedisCacheManager redisCacheManager = new RedisCacheManager(getRedisTemplate());
//		redisCacheManager.setDefaultExpiration(3000);
//		
//		DefaultRedisCachePrefix redisCachePrefix = new DefaultRedisCachePrefix();
//		redisCachePrefix.prefix("hcy");
//		redisCacheManager.setCachePrefix(redisCachePrefix);
//		
//		return redisCacheManager;
//	}
//	
//	public CompositeCacheManager getCompositeCacheManager(){
//		CompositeCacheManager compositeCacheManager = new CompositeCacheManager();
//		compositeCacheManager.setCacheManagers(Lists.newArrayList(getRedisCacheManager()));
//		//如果查询不到cacheManager,则不做任何缓存操作,直接查询数据库
//		compositeCacheManager.setFallbackToNoOpCache(true);
//		
//		return compositeCacheManager;
//	}
//}