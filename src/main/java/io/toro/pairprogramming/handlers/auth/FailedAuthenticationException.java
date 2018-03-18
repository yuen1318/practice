package io.toro.pairprogramming.handlers.auth;

public class FailedAuthenticationException extends RuntimeException {

  public FailedAuthenticationException(String message) {
    super(message);
  }
}
