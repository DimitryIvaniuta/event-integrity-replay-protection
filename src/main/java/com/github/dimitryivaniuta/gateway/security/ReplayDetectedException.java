package com.github.dimitryivaniuta.gateway.security;

/** Thrown when an event is identified as a replay (duplicate eventId within TTL window). */
public class ReplayDetectedException extends RuntimeException {

  public ReplayDetectedException(String message) {
    super(message);
  }
}
