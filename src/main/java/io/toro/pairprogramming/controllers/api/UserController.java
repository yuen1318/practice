package io.toro.pairprogramming.controllers.api;


import io.toro.pairprogramming.models.Project;

import io.toro.pairprogramming.handlers.auth.EmailAlreadyExistException;

import io.toro.pairprogramming.models.User;
import io.toro.pairprogramming.repositories.UserRepository;
import io.toro.pairprogramming.services.AuthService;
import io.toro.pairprogramming.services.UserService;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;

import io.toro.pairprogramming.services.projects.ProjectManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/users")
public class UserController {

    private UserService userService;
    private AuthService authService;
    private ProjectManager projectManager;
    private UserRepository userRepository;

    @Autowired
    public UserController(UserService userService,
        AuthService authService, UserRepository userRepository) {
        this.userService = userService;
        this.authService = authService;
        this.userRepository = userRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity getUser(@PathVariable Long id, HttpServletRequest request) {
        User user = userRepository.findOne(id);
        user = this.addUserLinks(user, request);
        return ResponseEntity.ok().body(user);
    }

    @GetMapping("")
    public ResponseEntity getAllUsers(HttpServletRequest request) {
        List<User> users = new ArrayList<>();
        for(User user : userRepository.findAll()) {
            user = this.addUserLinks(user, request);
            users.add(user);
        }
        return ResponseEntity.ok().body(users);
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody User userRequest, HttpServletRequest request) {
        Optional<User> registered = authService.register(userRequest);
        if(!registered.isPresent())
            throw new EmailAlreadyExistException("Email already exist");

        User user = registered.get();
        user = this.addUserLinks(user, request);
        URI location = URI.create(request.getRequestURL().toString() + "/" + user.getIds());
        return ResponseEntity.created(location).body(user);
    }

    private User addUserLinks(User user, HttpServletRequest request) {
        String baseUrl = "http://" + request.getServerName() + ":" + request.getServerPort() + "/api/v1/users";

        String selfUrl = baseUrl + "/" + user.getIds();
        Link selfLink = new Link(selfUrl, "self");

        String skillUrl = baseUrl + "/" + user.getIds() + "/" + "skills";
        Link skillLink = new Link(skillUrl, "skills");

        String projectUrl = baseUrl + "/" + user.getIds() + "/" + "projects";
        Link projectLink = new Link(projectUrl, "projects");
        user.add(selfLink, skillLink, projectLink);
        return user;
    }

}
