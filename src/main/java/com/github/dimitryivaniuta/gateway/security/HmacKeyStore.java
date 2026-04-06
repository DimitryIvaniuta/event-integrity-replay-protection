package com.github.dimitryivaniuta.gateway.security;

import java.util.Optional;

/** Abstraction for HMAC key management. */
public interface HmacKeyStore {

  /** @return active key ID used to sign new events. */
  String activeKeyId();

  /** Returns secret bytes for the given keyId. */
  Optional<byte[]> findSecret(String keyId);
}
