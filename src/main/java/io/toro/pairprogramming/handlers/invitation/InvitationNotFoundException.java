package io.toro.pairprogramming.handlers.invitation;

public class InvitationNotFoundException extends RuntimeException {

    public InvitationNotFoundException(String message){
        super(message);
    }
}
