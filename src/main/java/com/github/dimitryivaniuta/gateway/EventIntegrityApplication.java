package com.github.dimitryivaniuta.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Application entry point.
 *
 * <p>This service demonstrates:
 * <ul>
 *   <li>HMAC signed events (integrity)</li>
 *   <li>Key rotation (keyId embedded in the event)</li>
 *   <li>Replay protection (Redis TTL + Postgres unique constraint)</li>
 *   <li>Kafka dead-letter routing for invalid events</li>
 * </ul>
 */
@SpringBootApplication
public class EventIntegrityApplication {

  public static void main(String[] args) {
    SpringApplication.run(EventIntegrityApplication.class, args);
  }
}
