package com.github.dimitryivaniuta.gateway.api;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** Request payload to publish a secure event. */
public record PublishEventRequest(
    @NotBlank String eventType,
    @NotBlank String producerId,
    @NotBlank String aggregateId,
    @NotNull JsonNode payload
) {}
