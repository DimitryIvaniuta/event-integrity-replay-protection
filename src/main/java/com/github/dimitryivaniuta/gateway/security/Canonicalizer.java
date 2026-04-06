package com.github.dimitryivaniuta.gateway.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.UUID;

/**
 * Creates a canonical representation of an event that is stable across environments.
 */
public final class Canonicalizer {

  private final ObjectMapper objectMapper;

  public Canonicalizer(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  /** Builds canonical bytes for signing (excluding signature). */
  public byte[] canonicalBytes(
      int version,
      UUID eventId,
      String eventType,
      Instant occurredAt,
      String producerId,
      String aggregateId,
      JsonNode payload,
      String keyId
  ) {
    String payloadJson = toJson(payload);
    String canonical =
        version + "\n" +
        eventId + "\n" +
        eventType + "\n" +
        occurredAt.toString() + "\n" +
        producerId + "\n" +
        aggregateId + "\n" +
        keyId + "\n" +
        payloadJson;
    return canonical.getBytes(StandardCharsets.UTF_8);
  }

  private String toJson(JsonNode node) {
    try {
      return objectMapper.writeValueAsString(node);
    } catch (JsonProcessingException e) {
      throw new IllegalStateException("Failed to serialize payload for canonicalization", e);
    }
  }
}
