package io.toro.pairprogramming.handlers.directory;

public class DirectoryAlreadyExistsException extends RuntimeException {

    @Override
    public String getMessage() {
        return "Directory already exists";
    }
}
