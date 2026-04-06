package com.github.dimitryivaniuta.gateway.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.redis.testcontainers.RedisContainer;
import com.github.dimitryivaniuta.gateway.EventIntegrityApplication;
import com.github.dimitryivaniuta.gateway.config.AppProperties;
import com.github.dimitryivaniuta.gateway.domain.SecureEventEnvelope;
import com.github.dimitryivaniuta.gateway.persistence.ReceivedEventRepository;
import com.github.dimitryivaniuta.gateway.persistence.RejectedEventRepository;
import com.github.dimitryivaniuta.gateway.service.EventSigner;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

/**
 * End-to-end test:
 * - valid event -> received
 * - replay -> rejected
 * - forged -> rejected
 */
@SpringBootTest(classes = EventIntegrityApplication.class)
@EmbeddedKafka(partitions = 1, topics = {"secure-events", "secure-events.dlt"})
@Testcontainers
class SecureEventsIntegrationTest {

  @Container
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
      .withDatabaseName("event_integrity")
      .withUsername("app")
      .withPassword("app");

  @Container
  static RedisContainer redis = new RedisContainer("redis:7-alpine");

  @DynamicPropertySource
  static void props(DynamicPropertyRegistry r) {
    r.add("spring.datasource.url", postgres::getJdbcUrl);
    r.add("spring.datasource.username", postgres::getUsername);
    r.add("spring.datasource.password", postgres::getPassword);

    r.add("spring.data.redis.host", redis::getHost);
    r.add("spring.data.redis.port", () -> redis.getFirstMappedPort());

    r.add("spring.kafka.bootstrap-servers", () -> System.getProperty("spring.embedded.kafka.brokers"));
    r.add("app.replay-protection.ttl", () -> "2s");
    r.add("app.validation.max-event-age", () -> "15m");
    r.add("app.validation.max-future-skew", () -> "2m");
    r.add("app.admin.api-key", () -> "admin");
  }

  @Autowired KafkaTemplate<String, Object> kafkaTemplate;
  @Autowired AppProperties appProperties;
  @Autowired EventSigner signer;
  @Autowired ObjectMapper mapper;

  @Autowired ReceivedEventRepository receivedRepo;
  @Autowired RejectedEventRepository rejectedRepo;

  @Test
  void shouldAcceptValidAndRejectReplayAndForged() {
    ObjectNode payload = mapper.createObjectNode().put("x", 1);
    SecureEventEnvelope env = signer.signNew("TestEvent", "producer", "agg-1", payload);

    kafkaTemplate.send(appProperties.kafka().topic(), env.aggregateId(), env);

    await().atMost(Duration.ofSeconds(10)).untilAsserted(() ->
        assertThat(receivedRepo.findByEventId(env.eventId())).isPresent()
    );

    kafkaTemplate.send(appProperties.kafka().topic(), env.aggregateId(), env);

    await().atMost(Duration.ofSeconds(10)).untilAsserted(() ->
        assertThat(rejectedRepo.count()).isGreaterThanOrEqualTo(1)
    );

    SecureEventEnvelope forged = new SecureEventEnvelope(
        env.version(), env.eventId(), env.eventType(), env.occurredAt(),
        env.producerId(), env.aggregateId(), env.payload(), env.keyId(), env.signature() + "tamper"
    );
    kafkaTemplate.send(appProperties.kafka().topic(), forged.aggregateId(), forged);

    await().atMost(Duration.ofSeconds(10)).untilAsserted(() ->
        assertThat(rejectedRepo.count()).isGreaterThanOrEqualTo(2)
    );
  }
}
