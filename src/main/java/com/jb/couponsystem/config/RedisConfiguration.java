package com.jb.couponsystem.config;

import java.time.Duration;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
@EnableCaching
@PropertySource("classpath:application.properties")
public class RedisConfiguration extends CachingConfigurerSupport {
	
   
	  static final int SEC_TTL = 10;
	 
   
	   @Bean
	   JedisConnectionFactory jedisConnectionFactory() {
	       return new JedisConnectionFactory();
	   }
	    
	   @Bean
	   public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory rcf) {
	       RedisTemplate<String, Object> template = new RedisTemplate<>();
	       template.setConnectionFactory(rcf);
	       return template;
	   }
	   
       @Primary
	   @Bean
	   public CacheManager cacheManager(RedisConnectionFactory rcf) {
	       Duration expiration = Duration.ofSeconds(SEC_TTL);
	       return RedisCacheManager.builder(rcf)
	               .cacheDefaults(RedisCacheConfiguration.defaultCacheConfig().entryTtl(expiration)).build();
	       }
	   
	   
}
	   

