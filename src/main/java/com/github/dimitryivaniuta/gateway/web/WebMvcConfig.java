package com.github.dimitryivaniuta.gateway.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** Web MVC configuration. */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

  private final AdminApiKeyInterceptor adminApiKeyInterceptor;

  public WebMvcConfig(AdminApiKeyInterceptor adminApiKeyInterceptor) {
    this.adminApiKeyInterceptor = adminApiKeyInterceptor;
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(adminApiKeyInterceptor).addPathPatterns("/api/admin/**");
  }
}
