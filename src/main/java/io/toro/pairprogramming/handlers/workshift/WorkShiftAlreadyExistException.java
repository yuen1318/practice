package io.toro.pairprogramming.handlers.workshift;

public class WorkShiftAlreadyExistException extends RuntimeException{

    public WorkShiftAlreadyExistException() {
        super("workshift already exist!");
    }
}
