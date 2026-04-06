package com.github.dimitryivaniuta.gateway.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dimitryivaniuta.gateway.security.Canonicalizer;
import com.github.dimitryivaniuta.gateway.security.HmacKeyStore;
import com.github.dimitryivaniuta.gateway.security.HmacSigner;
import com.github.dimitryivaniuta.gateway.security.InMemoryHmacKeyStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Security beans configuration. */
@Configuration
public class SecurityBeansConfig {

  @Bean
  public HmacSigner hmacSigner() {
    return new HmacSigner();
  }

  @Bean
  public Canonicalizer canonicalizer(ObjectMapper objectMapper) {
    return new Canonicalizer(objectMapper);
  }

  @Bean
  public HmacKeyStore hmacKeyStore(SecurityHmacProperties props) {
    return new InMemoryHmacKeyStore(props);
  }
}
