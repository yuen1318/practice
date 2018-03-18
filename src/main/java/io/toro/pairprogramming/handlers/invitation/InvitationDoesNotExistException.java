package io.toro.pairprogramming.handlers.invitation;

public class InvitationDoesNotExistException extends RuntimeException {

    @Override
    public String getMessage() {
        return "Sadly, this invitation you tried deleting does not exist anymore."
                + "\\nHmmmm I know!"
                + "\\nWhy won't you try creating a new invitation and delete it again?";
    }
}
