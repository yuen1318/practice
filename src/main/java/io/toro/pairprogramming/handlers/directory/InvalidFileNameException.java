package io.toro.pairprogramming.handlers.directory;

public class InvalidFileNameException extends RuntimeException {

    @Override
    public String getMessage() {
        return "A file name can`t contain any of the following characters : \\/:*?\"<>|";
    }
}
