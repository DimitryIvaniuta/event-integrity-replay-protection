package com.github.dimitryivaniuta.gateway.security;

/** Thrown when event signature validation fails. */
public class SignatureValidationException extends RuntimeException {

  public SignatureValidationException(String message) {
    super(message);
  }

  public SignatureValidationException(String message, Throwable cause) {
    super(message, cause);
  }
}
