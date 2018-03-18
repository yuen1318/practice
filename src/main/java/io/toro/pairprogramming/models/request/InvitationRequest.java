package io.toro.pairprogramming.models.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class InvitationRequest {

    private String email;

    private String message;

}
