package io.toro.pairprogramming.handlers.directory;

public class FileAlreadyExistsException extends RuntimeException{

    @Override
    public String getMessage() {
        return "File already exists";
    }
}
