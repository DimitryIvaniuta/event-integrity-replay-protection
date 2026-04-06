package com.github.dimitryivaniuta.gateway.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.dimitryivaniuta.gateway.config.AppProperties;
import com.github.dimitryivaniuta.gateway.domain.SecureEventEnvelope;
import com.github.dimitryivaniuta.gateway.security.EventTimeValidationException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** Unit tests for event freshness validation. */
class EventTimeValidatorTest {

  private final AppProperties props = new AppProperties(
      new AppProperties.Kafka("secure-events", "secure-events.dlt"),
      new AppProperties.ReplayProtection(Duration.ofDays(7)),
      new AppProperties.Validation(Duration.ofMinutes(15), Duration.ofMinutes(2)),
      new AppProperties.Admin("admin")
  );

  @Test
  void shouldAcceptFreshEvent() {
    EventTimeValidator validator = new EventTimeValidator(
        Clock.fixed(Instant.parse("2026-04-06T12:00:00Z"), ZoneOffset.UTC),
        props
    );

    assertThatCode(() -> validator.validateOrThrow(envelopeAt("2026-04-06T11:55:00Z"))).doesNotThrowAnyException();
  }

  @Test
  void shouldRejectOldEvent() {
    EventTimeValidator validator = new EventTimeValidator(
        Clock.fixed(Instant.parse("2026-04-06T12:00:00Z"), ZoneOffset.UTC),
        props
    );

    assertThatThrownBy(() -> validator.validateOrThrow(envelopeAt("2026-04-06T11:40:00Z")))
        .isInstanceOf(EventTimeValidationException.class);
  }

  private SecureEventEnvelope envelopeAt(String occurredAt) {
    ObjectNode payload = new ObjectMapper().createObjectNode().put("x", 1);
    return new SecureEventEnvelope(
        1,
        UUID.randomUUID(),
        "TestEvent",
        Instant.parse(occurredAt),
        "producer",
        "agg-1",
        payload,
        "k1",
        "sig"
    );
  }
}
