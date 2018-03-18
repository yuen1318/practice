package io.toro.pairprogramming.controllers.api;

import java.sql.Timestamp;
import java.util.*;

import com.github.javafaker.Faker;
import io.toro.pairprogramming.models.*;
import io.toro.pairprogramming.repositories.*;
import io.toro.pairprogramming.services.AuthService;
import io.toro.pairprogramming.services.UserService;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.toro.pairprogramming.services.UserTimeZoneMatcherService;

@RestController
@RequestMapping("yuen")
public class DummyController {

    private UserRepository userRepository;
    private UserTimeZoneMatcherService userTimeZoneMatcherService;
    private AuthService authService;
    private Faker faker = new Faker();
    private UserSkillRepository userSkillRepository;
    private ProjectRepository projectRepository;
    private LanguageRepository languageRepository;
    private WorkShiftRepository workShiftRepository;
    @Autowired
    public DummyController( UserRepository userRepository,
            UserTimeZoneMatcherService userTimeZoneMatcherService,
                            AuthService authService,
                            UserSkillRepository userSkillRepository,
                            ProjectRepository projectRepository,
                            LanguageRepository languageRepository,
                            WorkShiftRepository workShiftRepository) {
        this.userRepository = userRepository;
        this.userTimeZoneMatcherService = userTimeZoneMatcherService;
        this.authService = authService;
        this.userSkillRepository = userSkillRepository;
        this.projectRepository = projectRepository;
        this.languageRepository = languageRepository;
        this.workShiftRepository = workShiftRepository;
    }

    @GetMapping
    public Boolean matchUserByTimeZone(){
        createDummyUsers();
        return true;
    }

    private void createDummyUsers() {
        int count = 10;
        while (count --> 0) {
            User user = new User();
            user.setFirstName(faker.name().firstName());
            user.setLastName(faker.name().firstName());
            user.setPassword(BCrypt.hashpw("123", BCrypt.gensalt()));
            user.setEmail(count + faker.internet().emailAddress());
            user = authService.register(user).get();
            createUserSkills(user);
            createDummyProjects(user);
            createDummyLanguages(user);
            createDummyWorkShift(user);
        }
    }

    private void createUserSkills(User user) {
        List<String> skills = Arrays.asList("php", "java");
        int count = 2;
        while (count --> 0) {
            UserSkill userSkill = new UserSkill();
            userSkill.setName(skills.get(count));
            userSkill.setLevel((new Random()).nextInt(10));
            userSkill.setUser(user);
            userSkillRepository.save(userSkill);
        }
    }

    private void createDummyProjects(User user) {
        int count = 2;
        while (count --> 0) {
            Project project = new Project();
            project.setName(count + faker.book().title());
            project.setUser(user);
            projectRepository.save(project);
        }
    }

    private void createDummyLanguages(User user) {
        List<String> languages = Arrays.asList("English", "Japanese", "Chinese");
        int count = 1;
        while (count --> 0) {
            Language language = new Language();
            language.setName(languages.get((new Random()).nextInt(2)));
            language = languageRepository.save(language);
            user.getLanguages().add(language);
            userRepository.save(user);
        }
    }

    private void createDummyWorkShift(User user) {
        Map<String, String> dum1 = new HashMap<>();
        dum1.put("timeZone", "Asia/Baghdad");
        dum1.put("startTime", "2011-01-01 5:00:00");
        dum1.put("endTime", "2011-01-01 15:00:00");

        Map<String, String> dum2 = new HashMap<>();
        dum2.put("timeZone", "Asia/Manila");
        dum2.put("startTime", "2011-01-01 09:10:10");
        dum2.put("endTime", "2011-01-01 21:10:10");

        Map<String, String> dum3 = new HashMap<>();
        dum3.put("timeZone", "Africa/Bangui");
        dum3.put("startTime", "2011-01-01 02:00:00");
        dum3.put("endTime", "2011-01-01 14:00:00");
        List<Map<String, String>> languages = new ArrayList<>();
        languages.add(dum1);
        languages.add(dum2);
        languages.add(dum3);

        int count = (new Random()).nextInt(3);
        WorkShift workShift = new WorkShift();
        workShift.setStartTime( Timestamp.valueOf( languages.get(count).get("startTime") ));
        workShift.setEndTime( Timestamp.valueOf( languages.get(count).get("endTime") ));
        workShift.setTimeZone( languages.get(count).get("timeZone")  );
        workShift.setUser(user);
        workShiftRepository.save(workShift);
    }
}
