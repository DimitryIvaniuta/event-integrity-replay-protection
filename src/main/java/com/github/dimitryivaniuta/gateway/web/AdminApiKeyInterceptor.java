package com.github.dimitryivaniuta.gateway.web;

import com.github.dimitryivaniuta.gateway.config.AppProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Protects internal admin endpoints with a simple API key.
 *
 * <p>This is intentionally lightweight for the sample project. In production, use proper service authentication.
 */
@Component
public class AdminApiKeyInterceptor implements HandlerInterceptor {

  public static final String HEADER = "X-Admin-Api-Key";

  private final AppProperties appProperties;

  public AdminApiKeyInterceptor(AppProperties appProperties) {
    this.appProperties = appProperties;
  }

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    String actual = request.getHeader(HEADER);
    String expected = appProperties.admin().apiKey();

    if (expected == null || expected.isBlank()) {
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Admin API key is not configured");
      return false;
    }

    if (actual == null || !MessageDigest.isEqual(actual.getBytes(StandardCharsets.UTF_8), expected.getBytes(StandardCharsets.UTF_8))) {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing or invalid admin API key");
      return false;
    }

    return true;
  }
}
