package com.github.dimitryivaniuta.gateway.security;

/** Thrown when an event timestamp is outside accepted freshness/skew limits. */
public class EventTimeValidationException extends RuntimeException {

  public EventTimeValidationException(String message) {
    super(message);
  }
}
