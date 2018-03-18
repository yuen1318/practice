package io.toro.pairprogramming.handlers.project;

public class ProjectEmptyException extends RuntimeException {

    public ProjectEmptyException() {
        super("project empty!");
    }
}
