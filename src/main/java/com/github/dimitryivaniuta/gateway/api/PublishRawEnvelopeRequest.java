package com.github.dimitryivaniuta.gateway.api;

import com.github.dimitryivaniuta.gateway.domain.SecureEventEnvelope;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/** Request wrapper used by the internal admin endpoint to publish a fully prepared envelope as-is. */
public record PublishRawEnvelopeRequest(@Valid @NotNull SecureEventEnvelope envelope) {}
