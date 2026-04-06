package com.github.dimitryivaniuta.gateway.config;

import jakarta.annotation.PostConstruct;
import java.util.Base64;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * Validates HMAC key configuration at startup.
 *
 * <p>This fails fast when the active key is missing or when configured secrets are too short to be acceptable.
 */
@Component
public class HmacKeyConfigurationValidator {

  private static final int MIN_SECRET_BYTES = 32;

  private final SecurityHmacProperties props;

  public HmacKeyConfigurationValidator(SecurityHmacProperties props) {
    this.props = props;
  }

  @PostConstruct
  void validate() {
    if (props.activeKeyId() == null || props.activeKeyId().isBlank()) {
      throw new IllegalStateException("security.hmac.activeKeyId must be configured");
    }

    if (props.keys() == null || props.keys().isEmpty()) {
      throw new IllegalStateException("security.hmac.keys must contain at least one key");
    }

    if (!props.keys().containsKey(props.activeKeyId())) {
      throw new IllegalStateException("Active keyId is missing from security.hmac.keys: " + props.activeKeyId());
    }

    for (Map.Entry<String, String> entry : props.keys().entrySet()) {
      byte[] decoded = Base64.getDecoder().decode(entry.getValue());
      if (decoded.length < MIN_SECRET_BYTES) {
        throw new IllegalStateException("HMAC key is too short for keyId=" + entry.getKey());
      }
    }
  }
}
