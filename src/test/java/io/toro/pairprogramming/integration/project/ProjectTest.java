package io.toro.pairprogramming.integration.project;

import io.toro.pairprogramming.integration.WithAuthenticationIntegrationTest;
import io.toro.pairprogramming.integration.utils.ProjectUtils;
import io.toro.pairprogramming.models.Project;
import io.toro.pairprogramming.models.User;
import io.toro.pairprogramming.services.projects.ProjectManager;
import org.apache.commons.beanutils.BeanUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProjectTest extends WithAuthenticationIntegrationTest {

    @Autowired
    ProjectManager projectManager;

    @Test
    public void shouldCreateProject() throws Exception {
        Project project = ProjectUtils.createProject();

        Project expected = (Project) BeanUtils.cloneBean(project);
        expected.setUser(user);
        //entityManager.persistAndFlush( expected );

        Map expected2 = objectMapper.convertValue(expected, Map.class);
        expected2.remove("id");

        mockMvc
                .perform(post("/api/v1/users/"+ user.getIds() + "/projects")
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(project)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(expected.getName())));
    }

    @Test
    public void shouldUpdateProject() throws Exception {
        Project project = ProjectUtils.createProject();
        project.setUser(user);

        entityManager.persistAndFlush(project);

        Project updatedProject = ProjectUtils.createProject();

        Project expected = (Project) BeanUtils.cloneBean(updatedProject);

        expected.setId(project.getIds());
        expected.setUser(user);
        expected.setType(project.getType());

        mockMvc
                .perform(put("/api/v1/users/"+ user.getIds() + "/projects/" + project.getIds())
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updatedProject)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(expected.getName())));
    }

    @Test
    public void shouldDeleteProject() throws Exception {
        Project project = ProjectUtils.createProject();
        project.setUser(user);

        projectManager.createProject(project);

        mockMvc
                .perform(delete("/api/v1/users/"+ user.getIds() + "/projects/" + project.getIds())
                            .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    public void shouldGetProjectById() throws Exception {
        Project project = ProjectUtils.createProject();
        project.setUser(user);

        entityManager.persistAndFlush(project);

        mockMvc
                .perform(get("/api/v1/users/"+ user.getIds() + "/projects/" + project.getIds())
                            .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(project.getIds().intValue())))
                .andExpect(jsonPath("$.name", is(project.getName())));
    }

    @Test
    public void shouldGetAllProjectOfUser() throws Exception {
        List<Project> projects = ProjectUtils.createProjects();
        user.setProjects(projects);
        entityManager.persistAndFlush(user);
        System.out.printf("\n\n%s\n\n", objectMapper.writeValueAsString(user));

        mockMvc
                .perform(get("/api/v1/users/"+ user.getIds() + "/projects")
                            .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(projects)));
    }
}