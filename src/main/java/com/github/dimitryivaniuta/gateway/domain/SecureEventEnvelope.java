package com.github.dimitryivaniuta.gateway.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

/**
 * Secure event envelope carried over Kafka.
 *
 * <p>All fields (except signature) participate in the canonical string that is signed via HMAC.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record SecureEventEnvelope(
    @NotNull Integer version,
    @NotNull UUID eventId,
    @NotBlank String eventType,
    @NotNull Instant occurredAt,
    @NotBlank String producerId,
    @NotBlank String aggregateId,
    @NotNull JsonNode payload,
    @NotBlank String keyId,
    @NotBlank String signature
) {}
