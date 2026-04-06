package com.github.dimitryivaniuta.gateway.api;

import java.util.UUID;

/** Response for internal admin publish operations. */
public record AdminPublishResponse(UUID eventId, String topic, String mode) {}
