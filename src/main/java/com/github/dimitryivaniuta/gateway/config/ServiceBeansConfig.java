package com.github.dimitryivaniuta.gateway.config;

import com.github.dimitryivaniuta.gateway.security.Canonicalizer;
import com.github.dimitryivaniuta.gateway.security.HmacKeyStore;
import com.github.dimitryivaniuta.gateway.security.HmacSigner;
import com.github.dimitryivaniuta.gateway.service.EventSigner;
import com.github.dimitryivaniuta.gateway.service.EventTimeValidator;
import com.github.dimitryivaniuta.gateway.service.EventVerifier;
import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Application service beans. */
@Configuration
public class ServiceBeansConfig {

  @Bean
  public Clock clock() {
    return Clock.systemUTC();
  }

  @Bean
  public EventSigner eventSigner(HmacKeyStore keyStore, Canonicalizer canonicalizer, HmacSigner signer) {
    return new EventSigner(keyStore, canonicalizer, signer);
  }

  @Bean
  public EventVerifier eventVerifier(HmacKeyStore keyStore, Canonicalizer canonicalizer, HmacSigner signer) {
    return new EventVerifier(keyStore, canonicalizer, signer);
  }

  @Bean
  public EventTimeValidator eventTimeValidator(Clock clock, AppProperties appProperties) {
    return new EventTimeValidator(clock, appProperties);
  }
}
