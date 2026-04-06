package com.github.dimitryivaniuta.gateway.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.dimitryivaniuta.gateway.domain.SecureEventEnvelope;
import com.github.dimitryivaniuta.gateway.security.Canonicalizer;
import com.github.dimitryivaniuta.gateway.security.HmacKeyStore;
import com.github.dimitryivaniuta.gateway.security.HmacSigner;
import com.github.dimitryivaniuta.gateway.security.SignatureValidationException;
import java.time.Instant;
import java.util.UUID;

/** Creates signed event envelopes. */
public class EventSigner {

  private final HmacKeyStore keyStore;
  private final Canonicalizer canonicalizer;
  private final HmacSigner signer;

  public EventSigner(HmacKeyStore keyStore, Canonicalizer canonicalizer, HmacSigner signer) {
    this.keyStore = keyStore;
    this.canonicalizer = canonicalizer;
    this.signer = signer;
  }

  /** Creates a new signed envelope using the active key. */
  public SecureEventEnvelope signNew(String eventType, String producerId, String aggregateId, JsonNode payload) {
    String keyId = keyStore.activeKeyId();
    byte[] secret = keyStore.findSecret(keyId)
        .orElseThrow(() -> new SignatureValidationException("Active key not found: " + keyId));

    UUID eventId = UUID.randomUUID();
    Instant occurredAt = Instant.now();
    int version = 1;

    byte[] canonical = canonicalizer.canonicalBytes(
        version, eventId, eventType, occurredAt, producerId, aggregateId, payload, keyId
    );

    String signature = signer.sign(canonical, secret);

    return new SecureEventEnvelope(
        version, eventId, eventType, occurredAt, producerId, aggregateId, payload, keyId, signature
    );
  }
}
