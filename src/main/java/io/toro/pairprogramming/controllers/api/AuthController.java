package io.toro.pairprogramming.controllers.api;

import io.toro.pairprogramming.handlers.auth.FailedAuthenticationException;
import io.toro.pairprogramming.models.Project;
import io.toro.pairprogramming.models.dto.AuthDTO;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.toro.pairprogramming.models.User;
import io.toro.pairprogramming.services.AuthService;

@RestController
public class AuthController {

    private AuthService authService;

    @Autowired
    AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/api/v1/auth/login")
    public ResponseEntity login(@RequestBody HashMap<String, String> credentials, HttpServletRequest request) {
        Optional<String> token = authService.login(credentials.get("email"), credentials.get("password"));

        if (token.isPresent()) {
            AuthDTO authDTO = (new AuthDTO()).setAccessToken(token.get()).bearerType();
            String selfUrl = request.getRequestURL().toString();
            Link selfLink = new Link(selfUrl, "self");
            authDTO.add(selfLink);
            return ResponseEntity.ok(authDTO);
        }

        throw new FailedAuthenticationException("Email/Passoword is incorrect");
    }
}
