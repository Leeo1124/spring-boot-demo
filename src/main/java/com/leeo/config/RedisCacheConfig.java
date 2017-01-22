package com.leeo.config;

import java.lang.reflect.Method;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.support.CompositeCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.DefaultRedisCachePrefix;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisTemplate;

import com.google.common.collect.Lists;

@Configuration
@EnableCaching
public class RedisCacheConfig extends CachingConfigurerSupport {

	@Bean
	public KeyGenerator wiselyKeyGenerator() {
		return new KeyGenerator() {
			@Override
			public Object generate(Object target, Method method, Object... params) {
				StringBuilder sb = new StringBuilder();
				sb.append(target.getClass().getName());
				sb.append(method.getName());
				for (Object obj : params) {
					sb.append(obj.toString());
				}
				return sb.toString();
			}
		};
	}

	@Bean
	public RedisCacheManager redisCacheManager(RedisTemplate<String, Object> redisTemplate) {
		RedisCacheManager redisCacheManager = new RedisCacheManager(redisTemplate);
//		redisCacheManager.setDefaultExpiration(3000);

		DefaultRedisCachePrefix redisCachePrefix = new DefaultRedisCachePrefix();
		redisCachePrefix.prefix("hcy");
		redisCacheManager.setCachePrefix(redisCachePrefix);

		return redisCacheManager;
	}

	@Bean
	public CacheManager cacheManager(RedisCacheManager redisCacheManager) {
		CompositeCacheManager compositeCacheManager = new CompositeCacheManager();
		compositeCacheManager.setCacheManagers(Lists.newArrayList(redisCacheManager));
		// 如果查询不到cacheManager,则不做任何缓存操作,直接查询数据库
		compositeCacheManager.setFallbackToNoOpCache(true);

		return compositeCacheManager;
	}

}