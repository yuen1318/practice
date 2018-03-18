package io.toro.pairprogramming.handlers.user;

public class UserDoesNotExistException extends RuntimeException {

    @Override
    public String getMessage() {
        return "User does not exist";
    }
}