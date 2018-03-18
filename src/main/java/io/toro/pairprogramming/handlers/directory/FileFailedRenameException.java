package io.toro.pairprogramming.handlers.directory;

public class FileFailedRenameException extends RuntimeException {

    @Override
    public String getMessage() {
        return "Failed to rename a file";
    }
}
