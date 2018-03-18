package io.toro.pairprogramming.handlers.user;

public class UserWithThisSkillDoesNotExistException extends RuntimeException {

    @Override
    public String getMessage() {
        return "Cannot find a user with this type of skill(s)";
    }
}