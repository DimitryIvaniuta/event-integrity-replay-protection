package com.github.dimitryivaniuta.gateway.service;

import com.github.dimitryivaniuta.gateway.domain.SecureEventEnvelope;
import com.github.dimitryivaniuta.gateway.security.Canonicalizer;
import com.github.dimitryivaniuta.gateway.security.HmacKeyStore;
import com.github.dimitryivaniuta.gateway.security.HmacSigner;
import com.github.dimitryivaniuta.gateway.security.SignatureValidationException;

/** Verifies event envelopes using the embedded keyId. */
public class EventVerifier {

  private final HmacKeyStore keyStore;
  private final Canonicalizer canonicalizer;
  private final HmacSigner signer;

  public EventVerifier(HmacKeyStore keyStore, Canonicalizer canonicalizer, HmacSigner signer) {
    this.keyStore = keyStore;
    this.canonicalizer = canonicalizer;
    this.signer = signer;
  }

  /** Validates signature. */
  public void verifyOrThrow(SecureEventEnvelope e) {
    byte[] secret = keyStore.findSecret(e.keyId())
        .orElseThrow(() -> new SignatureValidationException("Unknown keyId: " + e.keyId()));

    byte[] canonical = canonicalizer.canonicalBytes(
        e.version(), e.eventId(), e.eventType(), e.occurredAt(), e.producerId(), e.aggregateId(), e.payload(), e.keyId()
    );

    if (!signer.verify(canonical, secret, e.signature())) {
      throw new SignatureValidationException("Invalid signature for eventId=" + e.eventId());
    }
  }
}
