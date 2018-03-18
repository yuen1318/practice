package io.toro.pairprogramming.handlers.directory;

public class NotFileException extends RuntimeException {

    @Override
    public String getMessage() {
        return "Not a file";
    }
}
