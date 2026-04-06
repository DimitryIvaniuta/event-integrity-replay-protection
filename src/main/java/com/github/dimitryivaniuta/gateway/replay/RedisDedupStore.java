package com.github.dimitryivaniuta.gateway.replay;

import java.time.Duration;
import java.util.UUID;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * Redis-based dedup store using SETNX with TTL.
 *
 * <p>Key: dedup:event:{eventId}
 */
public class RedisDedupStore implements DedupStore {

  private final StringRedisTemplate redis;

  public RedisDedupStore(StringRedisTemplate redis) {
    this.redis = redis;
  }

  @Override
  public boolean markIfAbsent(UUID eventId, Duration ttl) {
    String key = "dedup:event:" + eventId;
    Boolean ok = redis.opsForValue().setIfAbsent(key, "1", ttl);
    return Boolean.TRUE.equals(ok);
  }
}
