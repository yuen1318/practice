package io.toro.pairprogramming.handlers.auth;

public class EmailAlreadyExistException extends RuntimeException {

  public EmailAlreadyExistException(String message) {
    super(message);
  }
}
