package com.github.dimitryivaniuta.gateway.security;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/** Unit tests for HMAC signer verification. */
class HmacSignerTest {

  @Test
  void shouldVerifySignature() {
    HmacSigner signer = new HmacSigner();
    byte[] secret = "secret-32-bytes-minimum-123456".getBytes(java.nio.charset.StandardCharsets.UTF_8);
    byte[] data = "hello".getBytes(java.nio.charset.StandardCharsets.UTF_8);

    String sig = signer.sign(data, secret);

    assertThat(signer.verify(data, secret, sig)).isTrue();
    assertThat(signer.verify("bye".getBytes(java.nio.charset.StandardCharsets.UTF_8), secret, sig)).isFalse();
  }
}
