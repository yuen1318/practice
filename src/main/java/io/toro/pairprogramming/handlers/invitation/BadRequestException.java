package io.toro.pairprogramming.handlers.invitation;

public class BadRequestException extends InvitationException{

    public BadRequestException(final String message) {
        super(message);
    }
}
