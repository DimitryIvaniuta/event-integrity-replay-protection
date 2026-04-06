package com.github.dimitryivaniuta.gateway.config;

import com.github.dimitryivaniuta.gateway.replay.DedupStore;
import com.github.dimitryivaniuta.gateway.replay.RedisDedupStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

/** Replay protection configuration. */
@Configuration
public class ReplayProtectionConfig {

  @Bean
  public DedupStore dedupStore(StringRedisTemplate redisTemplate) {
    return new RedisDedupStore(redisTemplate);
  }
}
