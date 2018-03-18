package io.toro.pairprogramming.handlers.directory;

public class DirectoryFailedDeleteException extends RuntimeException {

    @Override
    public String getMessage() {
        return "Failed to delete directory";
    }
}
