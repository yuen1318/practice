package io.toro.pairprogramming.controllers.api;

import io.toro.pairprogramming.handlers.ClientNotAuthorizedException;

import io.toro.pairprogramming.handlers.auth.ForbiddenActionException;

import io.toro.pairprogramming.models.Project;
import io.toro.pairprogramming.models.request.ProjectType;
import io.toro.pairprogramming.models.User;
import io.toro.pairprogramming.repositories.ProjectRepository;
import io.toro.pairprogramming.repositories.UserRepository;
import io.toro.pairprogramming.services.AuthService;
import io.toro.pairprogramming.services.UserMatchMakingService;
import io.toro.pairprogramming.services.UserService;
import io.toro.pairprogramming.services.projects.ProjectManager;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


@RestController
@RequestMapping("api/v1/users")
public class ProjectController {

    private ProjectManager projectManager;

    private UserService userService;

    private AuthService authService;

    private UserMatchMakingService userMatchMakingService;

    private UserRepository userRepository;

    private ProjectRepository projectRepository;

    public static final String baseUrl = "/api/v1/projects";

    @Autowired
    public ProjectController( ProjectManager projectManager, UserService userService,
            AuthService authService, UserMatchMakingService userMatchMakingService,
            UserRepository userRepository, ProjectRepository projectRepository ) {
        this.projectManager = projectManager;
        this.userService = userService;
        this.authService = authService;
        this.userMatchMakingService = userMatchMakingService;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
    }

    @GetMapping("/{userId}/projects")
    public ResponseEntity getProjects(@PathVariable("userId") Long userId, HttpServletRequest request) {
        User user = userService.getbyId(userId);
        List<Project> projects = new ArrayList<>();
        for(Project project : user.getProjects()) {
            project = this.addUserProjectLinks(project, request);
            projects.add(project);
        }
        return ResponseEntity.ok().body(projects);
    }

    @GetMapping("/{id}/projects/{projectId}/matching")
    public ResponseEntity matchSkills(@PathVariable Long projectId, @PathVariable Long id ) throws Exception {
        return ResponseEntity.status( HttpStatus.OK ).body( userMatchMakingService.matchPartnerToUser(projectId, id) );
    }

    @GetMapping("/{userId}/projects/{projectId}")
    public ResponseEntity findProjectById(@PathVariable Long projectId, HttpServletRequest request) {
        Project project = projectManager.getById(projectId);
        project = this.addUserProjectLinks(project, request);

        return ResponseEntity.ok(project);
    }

    @PostMapping("/{userId}/projects")
    public ResponseEntity create(@PathVariable Long userId, @RequestBody Project project, HttpServletRequest request) throws Exception {

        User userToken = authService.getUserFromToken(authService.getTokenFromHeader(request.getHeader("Authorization")));
        User user = userRepository.findOne(userId);


        if(userToken.getIds() != user.getIds())
            throw new ForbiddenActionException("Forbidden");

        try {
            project.setUser(user);
            project.setActiveEditor(user);
            Project createdProject = projectManager.createProject(project);

            createdProject = this.addUserProjectLinks(createdProject, request);
            URI location = URI.create(request.getRequestURL().toString() + "/" + createdProject.getIds());
            return ResponseEntity.created(location).body(createdProject);
        } catch (ClientNotAuthorizedException e) {
            HttpHeaders headers = new HttpHeaders();

            headers.setLocation(e.getAuthorizationUrl().toURI());

            return new ResponseEntity(headers, HttpStatus.FOUND);
        }
    }

    @PostMapping("/{userId}/projects/{projectId}/import")
    public ResponseEntity importProject(@PathVariable Long userId, @PathVariable Long projectId, @RequestBody Project project, HttpServletRequest request) throws Exception {
        User userToken = authService.getUserFromToken(authService.getTokenFromHeader(request.getHeader("Authorization")));
        User user = userRepository.findOne(userId);

        if(userToken.getIds() != user.getIds())
            throw new ForbiddenActionException("Forbidden");

        try {
            ProjectType type = project.getType();

            String repoName = project.getRepoName();

            projectManager.importProject(projectId, type, repoName);

            return ResponseEntity.noContent().build();
        } catch (ClientNotAuthorizedException e) {
            HttpHeaders headers = new HttpHeaders();

            headers.setLocation(e.getAuthorizationUrl().toURI());

            return new ResponseEntity(headers, HttpStatus.FOUND);
        }
    }

    @DeleteMapping("/{userId}/projects/{projectId}")
    public ResponseEntity delete(@PathVariable Long userId, @PathVariable Long projectId, HttpServletRequest request) throws Exception {

        User userToken = authService.getUserFromToken(authService.getTokenFromHeader(request.getHeader("Authorization")));
        User user = userRepository.findOne(userId);

        if(userToken.getIds() != user.getIds())
            throw new ForbiddenActionException("Forbidden");

        projectManager.deleteProject(projectId);
        return ResponseEntity.noContent().build();
    }


    @PutMapping("/{userId}/projects/{projectId}")
    public ResponseEntity update(@PathVariable Long userId, @PathVariable Long projectId, @RequestBody Project project, HttpServletRequest request) throws Exception {

        User userToken = authService.getUserFromToken(authService.getTokenFromHeader(request.getHeader("Authorization")));
        User user = userRepository.findOne(userId);

        if(userToken.getIds() != user.getIds())
            throw new ForbiddenActionException("Forbidden");

        try {
            Project updatedProject = projectManager.updateProject(projectId, project);
            updatedProject = this.addUserProjectLinks(updatedProject, request);
            return ResponseEntity.ok().body(updatedProject);
        } catch (ClientNotAuthorizedException e) {
            HttpHeaders headers = new HttpHeaders();

            headers.setLocation(e.getAuthorizationUrl().toURI());

            return new ResponseEntity(headers, HttpStatus.FOUND);
        }
    }

    @GetMapping("/{id}/active-editor")
    public ResponseEntity activeEditor(@PathVariable Long id, @RequestBody Project project, HttpServletRequest request) throws Exception {
        return ResponseEntity.ok().body(projectRepository.findOne(id).getActiveEditor());
    }

    private Project addUserProjectLinks(Project project, HttpServletRequest request) {
        String baseUrl = "http://" + request.getServerName() + ":" + request.getServerPort() + "/api/v1/users/" + project.getUser().getIds() + "/projects";

        String selfUrl = baseUrl + "/" + project.getIds();
        Link selfLink = new Link(selfUrl, "self");

        String importUrl = baseUrl + "/" + project.getIds() + "/import";
        Link importLink = new Link(importUrl, "import");

        String skillUrl = baseUrl + "/" + project.getIds() + "/skills";
        Link skillLink = new Link(skillUrl, "skills");

        String messageUrl = baseUrl + "/" + project.getIds() + "/messages";
        Link messageLink = new Link(messageUrl, "messages");

        project.add(selfLink, importLink, skillLink, messageLink);
        return project;
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
