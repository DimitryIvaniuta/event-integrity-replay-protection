package com.github.dimitryivaniuta.gateway.config;

import org.junit.jupiter.api.Test;

import java.util.Base64;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** Unit tests for startup HMAC key validation rules. */
class HmacKeyConfigurationValidatorTest {

  @Test
  void shouldAcceptValidConfiguration() {
    SecurityHmacProperties props = new SecurityHmacProperties(
        "k1",
        Map.of("k1", Base64.getEncoder().encodeToString("12345678901234567890123456789012".getBytes()))
    );

    assertThatCode(() -> new HmacKeyConfigurationValidator(props).validate()).doesNotThrowAnyException();
  }

  @Test
  void shouldRejectMissingActiveKey() {
    SecurityHmacProperties props = new SecurityHmacProperties(
        "k2",
        Map.of("k1", Base64.getEncoder().encodeToString("12345678901234567890123456789012".getBytes()))
    );

    assertThatThrownBy(() -> new HmacKeyConfigurationValidator(props).validate())
        .isInstanceOf(IllegalStateException.class);
  }
}
