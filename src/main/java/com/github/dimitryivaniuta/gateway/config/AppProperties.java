package com.github.dimitryivaniuta.gateway.config;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Application-specific properties.
 */
@ConfigurationProperties(prefix = "app")
public record AppProperties(
    Kafka kafka,
    ReplayProtection replayProtection,
    Validation validation,
    Admin admin
) {

  /** Kafka topic configuration. */
  public record Kafka(String topic, String dltTopic) {}

  /**
   * Replay protection settings.
   *
   * <p>ttl defines how long a processed eventId is considered "seen" in Redis.
   */
  public record ReplayProtection(Duration ttl) {}

  /**
   * Event age validation settings.
   *
   * <p>maxEventAge rejects very old events. maxFutureSkew rejects events that claim to be too far in the future.
   */
  public record Validation(Duration maxEventAge, Duration maxFutureSkew) {}

  /**
   * Internal admin access settings.
   *
   * <p>The admin API key is intended only for demo/internal endpoints. In production, prefer proper authentication.
   */
  public record Admin(String apiKey) {}
}
