package io.toro.pairprogramming.integration.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.toro.pairprogramming.models.Project;
import io.toro.pairprogramming.models.User;

import com.github.javafaker.Faker;

public class AuthUtils {

    public static User createUser() {
        Faker faker = new Faker();
        User user = new User();

        user.setFirstName(faker.name().firstName());
        user.setLastName(faker.name().lastName());
        user.setEmail(faker.internet().emailAddress());
        user.setPassword(faker.lorem().word());

        return user;
    }

    public static String createToken(String subject, String secret) throws Exception{
        return Jwts.builder()
                .signWith(SignatureAlgorithm.HS512, secret)
                .setSubject(subject)
                .compact();
    }

}
