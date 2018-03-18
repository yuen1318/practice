package io.toro.pairprogramming.controllers.api;

import io.toro.pairprogramming.handlers.auth.ForbiddenActionException;
import io.toro.pairprogramming.models.Project;
import io.toro.pairprogramming.models.ProjectSkill;
import io.toro.pairprogramming.models.User;
import io.toro.pairprogramming.models.UserSkill;
import io.toro.pairprogramming.repositories.ProjectRepository;
import io.toro.pairprogramming.repositories.ProjectSkillRepository;
import io.toro.pairprogramming.repositories.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;

import io.toro.pairprogramming.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/users")
public class ProjectSkillController {

    private ProjectRepository projectRepository;
    private ProjectSkillRepository projectSkillRepository;
    private UserRepository userRepository;
    private AuthService authService;

    @Autowired
    public ProjectSkillController(
        ProjectRepository projectRepository,
        ProjectSkillRepository projectSkillRepository,
        UserRepository userRepository,
        AuthService authService) {
        this.projectRepository = projectRepository;
        this.projectSkillRepository = projectSkillRepository;
        this.userRepository = userRepository;
        this.authService = authService;
    }

    @GetMapping("/{userId}/projects/{projectId}/skills")
    public ResponseEntity getSkills(@PathVariable("projectId") Long projectId, HttpServletRequest request) {

        Project project = projectRepository.findOne(projectId);
        List<ProjectSkill> projectSkills = new ArrayList<>();
        for(ProjectSkill projectSkill : project.getSkills()) {
            projectSkill = this.addProjectSkillLinks(projectSkill, request);
            projectSkills.add(projectSkill);
        }
        return ResponseEntity.ok().body(projectSkills);
    }

    @GetMapping("/{userId}/projects/{projectId}/skills/{skillId}")
    public ResponseEntity getSkill(@PathVariable("projectId") Long projectId, @PathVariable("skillId") Long skillId, HttpServletRequest request) {
        Project project = projectRepository.findOne(projectId);
        ProjectSkill projectSkill = projectSkillRepository.findOne(skillId);
        projectSkill = this.addProjectSkillLinks(projectSkill, request);
        return ResponseEntity.ok().body(projectSkill);
    }

    @PostMapping("/{userId}/projects/{projectId}/skills")
    public ResponseEntity addSkill(@PathVariable Long userId, @PathVariable("projectId") Long projectId, @RequestBody ProjectSkill skill, HttpServletRequest request) {

        User userToken = authService.getUserFromToken(authService.getTokenFromHeader(request.getHeader("Authorization")));
        User user = userRepository.findOne(userId);

        if(userToken.getIds() != user.getIds())
            throw new ForbiddenActionException("Forbidden");

        Project project = projectRepository.findOne(projectId);
        ProjectSkill projectSkill = this.createUserSkill(project, skill);
        projectSkill = this.addProjectSkillLinks(projectSkill, request);
        return ResponseEntity.ok().body(projectSkill);
    }

    @PutMapping("/{userId}/projects/{projectId}/skills/{skillId}")
    public ResponseEntity updateSkill(@PathVariable Long userId, @PathVariable("projectId") Long projectId, @PathVariable("skillId") Long skillId, @RequestBody ProjectSkill skill, HttpServletRequest request) {
        User userToken = authService.getUserFromToken(authService.getTokenFromHeader(request.getHeader("Authorization")));
        User user = userRepository.findOne(userId);

        if(userToken.getIds() != user.getIds())
            throw new ForbiddenActionException("Forbidden");

        Project project = projectRepository.findOne(projectId);
        ProjectSkill projectSkill = projectSkillRepository.findOne(skillId);
        projectSkill = this.updateProjectSkill(projectSkill, skill);
        projectSkill = this.addProjectSkillLinks(projectSkill, request);
        return ResponseEntity.ok().body(projectSkill);
    }

    @DeleteMapping("/{userId}/projects/{projectId}/skills/{skillId}")
    public ResponseEntity deleteSkill(@PathVariable Long userId, @PathVariable("skillId") Long skillId, HttpServletRequest request) {

        User userToken = authService.getUserFromToken(authService.getTokenFromHeader(request.getHeader("Authorization")));
        User user = userRepository.findOne(userId);

        if(userToken.getIds() != user.getIds())
            throw new ForbiddenActionException("Forbidden");

        projectSkillRepository.delete(skillId);
        return ResponseEntity.noContent().build();
    }

    private ProjectSkill updateProjectSkill(ProjectSkill projectSkill, ProjectSkill skill) {
        if(skill.getName() != null)
            projectSkill.setName(skill.getName());
        if(skill.getLevel() != null)
            projectSkill.setLevel(skill.getLevel());
        projectSkillRepository.save(projectSkill);
        return projectSkill;
    }

    private ProjectSkill createUserSkill(Project project, ProjectSkill skill) {
        ProjectSkill projectSkill = new ProjectSkill();
        projectSkill.setProject(project);
        projectSkill.setName(skill.getName());
        projectSkill.setLevel(skill.getLevel());
        projectSkillRepository.save(projectSkill);
        return projectSkill;
    }

    private ProjectSkill addProjectSkillLinks(ProjectSkill projectSkill, HttpServletRequest request) {
        String baseUrl = "http://" + request.getServerName() + ":" + request.getServerPort() + "/api/v1/users/"
            + projectSkill.getProject().getUser().getIds()
            + "/projects/" + projectSkill.getProject().getIds()
            + "/skills";

        String selfUrl = baseUrl + "/" + projectSkill.getIds();
        Link selfLink = new Link(selfUrl, "self");

        projectSkill.add(selfLink);
        return projectSkill;
    }
}
