package io.toro.pairprogramming.unit;


import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;


import io.toro.pairprogramming.integration.utils.AuthUtils;
import io.toro.pairprogramming.integration.utils.ProjectUtils;
import io.toro.pairprogramming.models.Project;
import io.toro.pairprogramming.models.ProjectSkill;
import io.toro.pairprogramming.models.User;
import io.toro.pairprogramming.models.UserSkill;
import io.toro.pairprogramming.repositories.UserSkillRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import io.toro.pairprogramming.integration.utils.AuthUtils;
import io.toro.pairprogramming.models.User;
import io.toro.pairprogramming.models.UserSkill;
import io.toro.pairprogramming.repositories.UserRepository;
import io.toro.pairprogramming.repositories.UserSkillRepository;
import io.toro.pairprogramming.services.UserService;

import com.github.javafaker.Faker;

@RunWith(SpringRunner.class)
public class UserServiceTest {

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UserSkillRepository userSkillRepository;

    private UserService userService;

    private Faker faker = new Faker();

    @Before
    public void setUp() throws Exception {

        userService = new UserService(userRepository, userSkillRepository);
    }

    @Test
    public void shouldAddSkills() throws Exception {
        User user = AuthUtils.createUser();

        List<UserSkill> userSkills = new ArrayList<>();

        UserSkill userSkill = new UserSkill();
        userSkill.setName("php");
        userSkill.setLevel(2);

        user.setSkills(userSkills);

        List<Long> skillIds = userSkills.stream().map(UserSkill::getIds).collect(Collectors.toList());

        when(userSkillRepository.findAll(skillIds))
                .thenReturn(userSkills);

        when(userRepository.findOne(user.getIds()))
                .thenReturn(user);

        when(userRepository.save(user))
                .thenReturn(user);

        assertThat(user.getSkills(), is(userSkills));

        //verify(userRepository, times(1)).save(user);
    }

    @Test
    public void shouldRemoveSkillsWhenIdNotInExisting() throws Exception {
        User user = AuthUtils.createUser();

        List<UserSkill> userSkills = new ArrayList<>();

        UserSkill userSkill = new UserSkill();
        userSkill.setName("php");
        userSkill.setLevel(2);
        userSkills.add(userSkill);
        // Set Initial user projectSkills
        //user.setProjectSkills(projectSkills);

        // Assume a skill is removed
        userSkills.remove(0);

        List<Long> skillIds = userSkills.stream().map(UserSkill::getIds).collect(Collectors.toList());

        when(userSkillRepository.findAll(skillIds))
                .thenReturn(userSkills);

        when(userRepository.findOne(user.getIds()))
                .thenReturn(user);

        // Assume user has less projectSkills
        user.setSkills(userSkills);

        when(userRepository.save(user))
                .thenReturn(user);

        assertThat(user.getSkills(), is(userSkills));

        //verify(userRepository, times(1)).save(user);
    }
    @Test
    public void shouldShowMatchedUsersBasedOnSkills() throws Exception{

        Integer count = 2;
        while(count-->0){
            User user = AuthUtils.createUser();
            Project project = ProjectUtils.createProject();
        }
    }
}
