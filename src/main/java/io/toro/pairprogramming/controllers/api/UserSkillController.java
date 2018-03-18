package io.toro.pairprogramming.controllers.api;

import io.toro.pairprogramming.handlers.auth.ForbiddenActionException;

import io.toro.pairprogramming.models.User;
import io.toro.pairprogramming.models.UserSkill;
import io.toro.pairprogramming.repositories.UserRepository;
import io.toro.pairprogramming.repositories.UserSkillRepository;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;

import io.toro.pairprogramming.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/users")
public class UserSkillController {

  private UserRepository userRepository;
  private UserSkillRepository userSkillRepository;
  private AuthService authService;
  @Autowired
  public UserSkillController(UserRepository userRepository, UserSkillRepository userSkillRepository, AuthService authService) {
    this.userRepository = userRepository;
    this.userSkillRepository = userSkillRepository;
    this.authService = authService;
  }

  @GetMapping("/{userId}/skills")
  public ResponseEntity getSkills(@PathVariable("userId") Long userId, HttpServletRequest request) {
    User user = userRepository.findOne(userId);
    List<UserSkill> userSkills = new ArrayList<>();
    for(UserSkill userSkill : user.getSkills()) {
      userSkill = this.addUserSkillLinks(userSkill, request);
      userSkills.add(userSkill);
    }
    return ResponseEntity.ok().body(userSkills);
  }

  @GetMapping("/{userId}/skills/{skillId}")
  public ResponseEntity getSkill(@PathVariable("userId") Long userId, @PathVariable("skillId") Long skillId, HttpServletRequest request) {
    User user = userRepository.findOne(userId);
    UserSkill userSkill = userSkillRepository.findOne(skillId);
    userSkill = this.addUserSkillLinks(userSkill, request);
    return ResponseEntity.ok().body(userSkill);

  }

  @PostMapping("/{userId}/skills")
  public ResponseEntity addSkill(@PathVariable("userId") Long userId, @RequestBody UserSkill skill, HttpServletRequest request) {

    User userToken = authService.getUserFromToken(authService.getTokenFromHeader(request.getHeader("Authorization")));
    User user = userRepository.findOne(userId);

    if(userToken.getIds() != user.getIds())
      throw new ForbiddenActionException("Forbidden");

    UserSkill userSkill = this.createUserSkill(user, skill);
    userSkill = this.addUserSkillLinks(userSkill, request);
    URI location = URI.create(request.getRequestURL().toString() + "/" + userSkill.getIds());
    return ResponseEntity.created(location).body(userSkill);
  }

  @PutMapping("/{userId}/skills/{skillId}")
  public ResponseEntity updateSkill(@PathVariable("userId") Long userId, @PathVariable("skillId") Long skillId, @RequestBody UserSkill skill, HttpServletRequest request) {
    User userToken = authService.getUserFromToken(authService.getTokenFromHeader(request.getHeader("Authorization")));
    User user = userRepository.findOne(userId);

    if(userToken.getIds() != user.getIds())
      throw new ForbiddenActionException("Forbidden");

    UserSkill userSkill = userSkillRepository.findOne(skillId);
    userSkill = this.updateUserSkill(userSkill, skill);
    userSkill = this.addUserSkillLinks(userSkill, request);
    return ResponseEntity.ok().body(userSkill);
  }

  @DeleteMapping("/{userId}/skills/{skillId}")
  public ResponseEntity deleteSkill(@PathVariable("userId") Long userId, @PathVariable("skillId") Long skillId, HttpServletRequest request) {
    User userToken = authService.getUserFromToken(authService.getTokenFromHeader(request.getHeader("Authorization")));
    User user = userRepository.findOne(userId);

    if(userToken.getIds() != user.getIds())
      throw new ForbiddenActionException("Forbidden");

    UserSkill userSkill = userSkillRepository.findOne(skillId);
    userSkillRepository.delete(userSkill.getIds());
    return ResponseEntity.noContent().build();
  }

  private UserSkill updateUserSkill(UserSkill userSkill, UserSkill skill) {
    if(skill.getName() != null)
      userSkill.setName(skill.getName());
    if(skill.getLevel() != null)
      userSkill.setLevel(skill.getLevel());
    userSkillRepository.save(userSkill);
    return userSkill;
  }

  private UserSkill createUserSkill(User user, UserSkill skill) {
    UserSkill userSkill = new UserSkill();
    userSkill.setUser(user);
    userSkill.setName(skill.getName());
    userSkill.setLevel(skill.getLevel());
    userSkillRepository.save(userSkill);
    return userSkill;
  }

  private UserSkill addUserSkillLinks(UserSkill userSkill, HttpServletRequest request) {
    String baseUrl = "http://" + request.getServerName() + ":" + request.getServerPort() + "/api/v1/users/" + userSkill.getUser().getIds();

    String selfUrl = baseUrl + "/" + "skills/" + userSkill.getIds();
    Link selfLink = new Link(selfUrl, "self");

    userSkill.add(selfLink);
    return userSkill;
  }
}
