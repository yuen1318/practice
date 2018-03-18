package io.toro.pairprogramming.integration.user;

import io.toro.pairprogramming.integration.BaseIntegrationTest;
import io.toro.pairprogramming.integration.utils.SkillUtils;
import io.toro.pairprogramming.models.Project;
import io.toro.pairprogramming.models.ProjectSkill;
import io.toro.pairprogramming.models.User;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;

import java.util.List;

import static io.toro.pairprogramming.integration.utils.AuthUtils.createToken;
import static io.toro.pairprogramming.integration.utils.AuthUtils.createUser;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProjectSkillTest extends BaseIntegrationTest {

    private String token;
    private User user;

    @Before
    public void setUp() throws Exception {
        user = entityManager.persistAndFlush(createUser());

        String subject = objectMapper.writeValueAsString(user);

        token = createToken(subject, secret);
    }

    @Test
    public void shouldAddSkills() throws Exception {
        Project project = new Project();
        project.setUser(user);
        project.setName("dictionary");

        entityManager.persistAndFlush(project);

        ProjectSkill projectSkill = new ProjectSkill();
        projectSkill.setName("php");
        projectSkill.setLevel(2);
        mockMvc
                .perform(post("/api/v1/users/" + user.getIds() + "/projects/" + project.getIds() + "/skills")
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(projectSkill)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(projectSkill.getName())));
    }

    @Test
    public void shouldRemoveSkills() throws Exception {
        Project project = new Project();
        project.setUser(user);
        project.setName("dictionary");

        entityManager.persistAndFlush(project);

        ProjectSkill projectSkill = new ProjectSkill();
        projectSkill.setName("php");
        projectSkill.setLevel(2);
        projectSkill.setProject(project);

        entityManager.persistAndFlush(projectSkill);

        mockMvc
            .perform(delete("/api/v1/users/" + user.getIds() + "/projects/" + project.getIds() + "/skills/" + projectSkill.getIds())
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isNoContent());
            //.andExpect(jsonPath("$.projectSkills[0].name", is(projectSkills.get(0))));
    }
}
