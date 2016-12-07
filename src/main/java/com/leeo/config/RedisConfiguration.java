package com.leeo.config;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties.Cluster;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import redis.clients.jedis.JedisPoolConfig;

/**
 * redis 配置
 *
 */
@Configuration
public class RedisConfiguration {

	private static final Logger logger = LoggerFactory.getLogger(RedisConfiguration.class);

	@Autowired
	private RedisProperties redisProperties;

	/**
	 * jedis 配置
	 * 
	 * @return
	 */
	@Bean
	public JedisPoolConfig jedisPoolConfig() {
		JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
		RedisProperties.Pool pool = this.redisProperties.getPool();
		jedisPoolConfig.setMaxTotal(pool.getMaxActive());
		jedisPoolConfig.setMaxIdle(pool.getMaxIdle());
		jedisPoolConfig.setMinIdle(pool.getMinIdle());
		jedisPoolConfig.setMaxWaitMillis(pool.getMaxWait());
		jedisPoolConfig.setTestOnBorrow(true);
		jedisPoolConfig.setTestWhileIdle(true);

		return jedisPoolConfig;
	}

	/**
	 * 集群配置
	 * 
	 * @return
	 */
	@Bean
	public RedisClusterConfiguration redisClusterConfiguration() {
		if (this.redisProperties.getCluster() == null) {
			return null;
		}
		Cluster clusterProperties = this.redisProperties.getCluster();
		RedisClusterConfiguration config = new RedisClusterConfiguration(clusterProperties.getNodes());

		if (clusterProperties.getMaxRedirects() != null) {
			config.setMaxRedirects(clusterProperties.getMaxRedirects());
		}

		return config;
	}

	/**
	 * redis服务器中心
	 * 
	 * @return
	 */
	@Bean
	public JedisConnectionFactory jedisConnectionFactory(JedisPoolConfig jedisPoolConfig,
			RedisClusterConfiguration redisClusterConfiguration) {
		JedisConnectionFactory jedisConnectionFactory = null;
		if (null != redisClusterConfiguration) {
			jedisConnectionFactory = new JedisConnectionFactory(redisClusterConfiguration);
		} else {
			jedisConnectionFactory = new JedisConnectionFactory();
		}

		jedisConnectionFactory.setPoolConfig(jedisPoolConfig);
		jedisConnectionFactory.setPort(this.redisProperties.getPort());
		jedisConnectionFactory.setHostName(this.redisProperties.getHost());
		if (this.redisProperties.getDatabase() > 0) {
			jedisConnectionFactory.setDatabase(this.redisProperties.getDatabase());
		}
		if (StringUtils.isNotBlank(this.redisProperties.getPassword())) {
			jedisConnectionFactory.setPassword(this.redisProperties.getPassword());
		}
		if (this.redisProperties.getTimeout() > 0) {
			jedisConnectionFactory.setTimeout(this.redisProperties.getTimeout());
		}

		return jedisConnectionFactory;
	}

	@Bean
	public RedisTemplate<String, Object> redisTemplate(JedisConnectionFactory jedisConnectionFactory) {
		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(jedisConnectionFactory);
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		// redisTemplate.setValueSerializer(new
		// JdkSerializationRedisSerializer());
		//开启事务
		redisTemplate.setEnableTransactionSupport(true);

		return redisTemplate;
	}

	// /**
	// * redisCacheManager配置
	// * @return
	// */
	// public RedisCacheManager getRedisCacheManager(){
	// RedisCacheManager redisCacheManager = new
	// RedisCacheManager(getRedisTemplate());
	// redisCacheManager.setDefaultExpiration(3000);
	//
	// DefaultRedisCachePrefix redisCachePrefix = new DefaultRedisCachePrefix();
	// redisCachePrefix.prefix("hcy");
	// redisCacheManager.setCachePrefix(redisCachePrefix);
	//
	// return redisCacheManager;
	// }
	//
	// public CompositeCacheManager getCompositeCacheManager(){
	// CompositeCacheManager compositeCacheManager = new
	// CompositeCacheManager();
	// compositeCacheManager.setCacheManagers(Lists.newArrayList(getRedisCacheManager()));
	// //如果查询不到cacheManager,则不做任何缓存操作,直接查询数据库
	// compositeCacheManager.setFallbackToNoOpCache(true);
	//
	// return compositeCacheManager;
	// }
}