package com.github.dimitryivaniuta.gateway.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/** Enables binding of application configuration properties. */
@Configuration
@EnableConfigurationProperties({AppProperties.class, SecurityHmacProperties.class})
public class PropertiesConfig {}
