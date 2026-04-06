package com.github.dimitryivaniuta.gateway.api;

import java.util.UUID;

/** Response after publishing an event. */
public record PublishEventResponse(UUID eventId, String topic) {}
