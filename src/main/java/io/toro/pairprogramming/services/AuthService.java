package io.toro.pairprogramming.services;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Optional;

import org.apache.commons.beanutils.BeanUtils;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.toro.pairprogramming.models.User;
import io.toro.pairprogramming.repositories.UserRepository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AuthService {

    private UserRepository userRepository;

    private String secret;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public AuthService(UserRepository userRepository, @Value("${secret}") String secret) {
        this.userRepository = userRepository;
        this.secret = secret;
    }

    public Optional<String> login(String email, String password) {
        Boolean exists = userRepository.existsByEmail(email);

        if (exists) {
            User user = userRepository.findByEmailContainingIgnoreCase(email);

            String token = null;

            try {
                token = Jwts.builder()
                        .signWith(SignatureAlgorithm.HS512, secret)
                        .setSubject(objectMapper.writeValueAsString(user))
                        .compact();
            } catch (JsonProcessingException ignored) {
                // We checked if User already exists
                // It can never be empty.
            }

            return BCrypt.checkpw(password, user.getPassword())
                    ? Optional.of(token)
                    : Optional.empty();
        }

        return Optional.empty();
    }

    public Optional<User> register(User user) {
        user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));

        return userRepository.existsByEmail(user.getEmail())
                ? Optional.empty()
                : Optional.of(userRepository.save(user));
    }

    public Boolean verifyToken(String token) {
        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            return true;
        } catch (SignatureException e) {
            return false;
        }
    }

    public String getSecret() {
        return secret;
    }

    public User getUserFromToken(String token) {
        String jsonUser = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getSubject();

        User user = new User();

        try {
            // objectMapper will not write ID because it is readonly
            // we instead convert a generic HashMap to User using BeanUtils.
            TypeReference<HashMap<String,Object>> typeref = new TypeReference<HashMap<String, Object>>() {};

            HashMap<String,Object> rawUser = objectMapper.readValue(jsonUser, typeref);
            user.setId(((Integer) rawUser.get("id")).longValue());
            user.setFirstName((String) rawUser.get("firstName"));
            user.setLastName((String) rawUser.get("lastName"));
            user.setEmail((String) rawUser.get("email"));
            //BeanUtils.populate(user, rawUser);
        } catch (IOException ignored) {
            // Token is verified before-hand. We are sure it is not tampered.
        }

        return user;
    }

    public String getTokenFromHeader(String header) {
        return header.substring(7);
    }
}
