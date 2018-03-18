package io.toro.pairprogramming.handlers.invitation;

public class InvitationIsAlreadyExistingException extends RuntimeException {

    public InvitationIsAlreadyExistingException(String message){
        super(message);
    }
}
