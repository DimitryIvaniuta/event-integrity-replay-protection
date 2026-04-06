package com.github.dimitryivaniuta.gateway.security;

import com.github.dimitryivaniuta.gateway.config.SecurityHmacProperties;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * In-memory key store based on configuration properties.
 *
 * <p>Do NOT store secrets in config for real production.
 */
public class InMemoryHmacKeyStore implements HmacKeyStore {

  private final SecurityHmacProperties props;
  private final Map<String, byte[]> secrets;

  public InMemoryHmacKeyStore(SecurityHmacProperties props) {
    this.props = props;
    this.secrets = decode(props.keys());
  }

  @Override
  public String activeKeyId() {
    return props.activeKeyId();
  }

  @Override
  public Optional<byte[]> findSecret(String keyId) {
    return Optional.ofNullable(secrets.get(keyId));
  }

  private static Map<String, byte[]> decode(Map<String, String> keys) {
    return keys.entrySet().stream()
        .collect(Collectors.toUnmodifiableMap(
            Map.Entry::getKey,
            e -> Base64.getDecoder().decode(e.getValue())
        ));
  }
}
