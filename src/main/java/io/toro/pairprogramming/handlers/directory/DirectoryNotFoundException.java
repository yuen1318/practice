package io.toro.pairprogramming.handlers.directory;

import java.io.FileNotFoundException;

public class DirectoryNotFoundException extends FileNotFoundException {

    @Override
    public String getMessage() {
        return "Failed to rename directory";
    }
}
