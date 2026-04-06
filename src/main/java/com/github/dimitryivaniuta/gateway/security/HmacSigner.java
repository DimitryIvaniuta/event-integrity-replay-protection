package com.github.dimitryivaniuta.gateway.security;

import java.security.MessageDigest;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/** Signs and verifies byte arrays using HMAC-SHA256. */
public final class HmacSigner {

  private static final String ALG = "HmacSHA256";

  /** Computes base64 signature for given data and secret. */
  public String sign(byte[] data, byte[] secret) {
    try {
      Mac mac = Mac.getInstance(ALG);
      mac.init(new SecretKeySpec(secret, ALG));
      byte[] out = mac.doFinal(data);
      return Base64.getEncoder().encodeToString(out);
    } catch (Exception e) {
      throw new IllegalStateException("HMAC signing failed", e);
    }
  }

  /** Constant-time verification. */
  public boolean verify(byte[] data, byte[] secret, String expectedBase64Signature) {
    String actual = sign(data, secret);
    return MessageDigest.isEqual(
        actual.getBytes(java.nio.charset.StandardCharsets.UTF_8),
        expectedBase64Signature.getBytes(java.nio.charset.StandardCharsets.UTF_8)
    );
  }
}
