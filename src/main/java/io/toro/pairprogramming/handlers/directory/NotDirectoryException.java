package io.toro.pairprogramming.handlers.directory;

public class NotDirectoryException extends RuntimeException {

    @Override
    public String getMessage() {
        return "Not a directory";
    }
}
