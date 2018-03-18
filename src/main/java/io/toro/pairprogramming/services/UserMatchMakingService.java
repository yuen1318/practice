package io.toro.pairprogramming.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.toro.pairprogramming.models.ProjectSkill;
import io.toro.pairprogramming.models.User;
import io.toro.pairprogramming.repositories.ProjectRepository;


@Service
public class UserMatchMakingService {

    private UserSkillsWeightMatcherService userSkillsWeightMatcherService;
    private UserTimeZoneMatcherService userTimeZoneMatcherService;
    private UserService userService;
    private ProjectRepository projectRepository;

    @Autowired
    public UserMatchMakingService(
            UserSkillsWeightMatcherService userSkillsWeightMatcherService,
            UserTimeZoneMatcherService userTimeZoneMatcherService,
            UserService userService, ProjectRepository projectRepository ) {
        this.userSkillsWeightMatcherService = userSkillsWeightMatcherService;
        this.userTimeZoneMatcherService = userTimeZoneMatcherService;
        this.userService = userService;
        this.projectRepository = projectRepository;
    }

    public List<User> matchPartnerToUser(Long projectId, Long userId){
        List<String> byProjectSkills = projectRepository.findOne(projectId)
                .getSkills().stream().map( ProjectSkill::getName )
                .collect(Collectors.toList());
        List<User> filterListBySkillsAndSpokenLanguage = userService.match( projectId ).stream()
                .filter( o -> o.getIds() != userId )
                .collect( Collectors.toList());
        List<User> filterListByMatchingTimeZone = userTimeZoneMatcherService.matchPartnerToUserByTimeZone( filterListBySkillsAndSpokenLanguage, userService.getbyId( userId ) );
        return userSkillsWeightMatcherService.sortByLevelDesc( filterListByMatchingTimeZone, byProjectSkills);
    }


}
