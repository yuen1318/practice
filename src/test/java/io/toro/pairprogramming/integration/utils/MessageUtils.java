package io.toro.pairprogramming.integration.utils;

import io.toro.pairprogramming.models.Message;
import io.toro.pairprogramming.models.Project;
import io.toro.pairprogramming.models.User;

import com.github.javafaker.Faker;

public class MessageUtils {

    public static Message createMessage(Project project, User user){
        Faker faker = new Faker();
        Message message = new Message();
        message.setId( faker.number().randomNumber() );
        message.setUser(user);
        message.setProject(project);
        message.setMessage("{\"message\" : \""+ faker.lorem().sentence( 1 ) + "\"}");
        return message;
    }
}
