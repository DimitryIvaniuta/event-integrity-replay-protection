package com.github.dimitryivaniuta.gateway.service;

import com.github.dimitryivaniuta.gateway.config.AppProperties;
import com.github.dimitryivaniuta.gateway.domain.SecureEventEnvelope;
import com.github.dimitryivaniuta.gateway.security.EventTimeValidationException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

/**
 * Validates that event timestamps are still acceptable for processing.
 *
 * <p>This reduces the usefulness of captured historical events because even a correctly signed event will be rejected
 * once it falls outside the accepted age window.
 */
public class EventTimeValidator {

  private final Clock clock;
  private final AppProperties props;

  public EventTimeValidator(Clock clock, AppProperties props) {
    this.clock = clock;
    this.props = props;
  }

  /** Validates that the event timestamp is not too old and not too far in the future. */
  public void validateOrThrow(SecureEventEnvelope envelope) {
    Instant now = clock.instant();
    Instant occurredAt = envelope.occurredAt();

    Duration maxEventAge = props.validation().maxEventAge();
    Duration maxFutureSkew = props.validation().maxFutureSkew();

    if (occurredAt.isBefore(now.minus(maxEventAge))) {
      throw new EventTimeValidationException("Event is too old for processing: eventId=" + envelope.eventId());
    }

    if (occurredAt.isAfter(now.plus(maxFutureSkew))) {
      throw new EventTimeValidationException("Event timestamp is too far in the future: eventId=" + envelope.eventId());
    }
  }
}
