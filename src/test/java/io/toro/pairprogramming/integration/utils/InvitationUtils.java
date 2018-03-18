package io.toro.pairprogramming.integration.utils;

import java.util.ArrayList;
import java.util.List;

import com.github.javafaker.Faker;
import io.toro.pairprogramming.models.Invitation;
import io.toro.pairprogramming.models.InvitationStatus;
import io.toro.pairprogramming.models.Project;
import io.toro.pairprogramming.models.User;

import static io.toro.pairprogramming.models.InvitationStatus.ACCEPT;
import static io.toro.pairprogramming.models.InvitationStatus.DECLINE;
import static io.toro.pairprogramming.models.InvitationStatus.PENDING;

public class InvitationUtils {

    public static Invitation createInvitation(){
        Faker faker = new Faker();
        Invitation invitation = new Invitation();

        invitation.setMessage(faker.gameOfThrones().quote());
        invitation.setStatus(PENDING);

        return invitation;
    }

}
