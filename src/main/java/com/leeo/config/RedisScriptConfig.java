package com.leeo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;

@Configuration
public class RedisScriptConfig {

	@Bean(name = "ratelimiter")
	public DefaultRedisScript<Boolean> defaultRedisScript() {
		DefaultRedisScript<Boolean> defaultRedisScript = new DefaultRedisScript<>();
		defaultRedisScript.setLocation(new ClassPathResource("lua/ratelimiter.lua"));
		defaultRedisScript.setResultType(Boolean.class);

		return defaultRedisScript;
	}
}