package com.github.dimitryivaniuta.gateway.config;

import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * HMAC key configuration.
 *
 * <p>Keys are configured as keyId -> base64(secretBytes).
 * activeKeyId is used by the producer to sign new events.
 */
@ConfigurationProperties(prefix = "security.hmac")
public record SecurityHmacProperties(
    String activeKeyId,
    Map<String, String> keys
) {}
