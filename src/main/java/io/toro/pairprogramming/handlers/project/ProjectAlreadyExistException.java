package io.toro.pairprogramming.handlers.project;

public class ProjectAlreadyExistException extends RuntimeException {

    public ProjectAlreadyExistException(String message) {
        super(message);
    }
}
