package com.github.dimitryivaniuta.gateway.api;

import com.github.dimitryivaniuta.gateway.security.SignatureValidationException;
import java.time.Instant;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Maps common exceptions to consistent JSON responses. */
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex) {
    return ResponseEntity.badRequest().body(Map.of(
        "timestamp", Instant.now().toString(),
        "status", 400,
        "error", "Bad Request",
        "message", "Validation failed"
    ));
  }

  @ExceptionHandler(SignatureValidationException.class)
  public ResponseEntity<?> handleSignature(SignatureValidationException ex) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
        "timestamp", Instant.now().toString(),
        "status", 500,
        "error", "Internal Server Error",
        "message", ex.getMessage()
    ));
  }
}
