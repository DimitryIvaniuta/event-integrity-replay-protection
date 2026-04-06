package com.github.dimitryivaniuta.gateway.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.dimitryivaniuta.gateway.security.Canonicalizer;
import com.github.dimitryivaniuta.gateway.security.HmacKeyStore;
import com.github.dimitryivaniuta.gateway.security.HmacSigner;
import com.github.dimitryivaniuta.gateway.security.SignatureValidationException;
import java.util.Optional;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** Unit test: tampering payload breaks signature validation. */
class EventSignerVerifierTest {

  @Test
  void shouldRejectTamperedPayload() {
    ObjectMapper mapper = new ObjectMapper();
    Canonicalizer canonicalizer = new Canonicalizer(mapper);
    HmacSigner signer = new HmacSigner();

    byte[] secret = "dev-secret-please-change-k1-32bytes!!".getBytes(java.nio.charset.StandardCharsets.UTF_8);
    String keyId = "k1";

    HmacKeyStore keyStore = new HmacKeyStore() {
      @Override public String activeKeyId() { return keyId; }
      @Override public Optional<byte[]> findSecret(String id) { return Optional.of(secret); }
    };

    EventSigner eventSigner = new EventSigner(keyStore, canonicalizer, signer);
    EventVerifier verifier = new EventVerifier(keyStore, canonicalizer, signer);

    ObjectNode payload = mapper.createObjectNode().put("a", 1);
    var env = eventSigner.signNew("TestEvent", "producer", "agg-1", payload);

    payload.put("a", 2);

    assertThatThrownBy(() -> verifier.verifyOrThrow(env))
        .isInstanceOf(SignatureValidationException.class);
  }
}
