package io.toro.pairprogramming.unit;

import io.toro.pairprogramming.integration.utils.AuthUtils;
import io.toro.pairprogramming.integration.utils.ProjectUtils;
import io.toro.pairprogramming.models.Project;
import io.toro.pairprogramming.models.User;
import io.toro.pairprogramming.repositories.ProjectRepository;
import io.toro.pairprogramming.services.projects.ProjectManager;
import io.toro.pairprogramming.services.projects.clients.RemoteRepoClient;
import io.toro.pairprogramming.services.projects.factories.RemoteClientCreator;
import io.toro.pairprogramming.services.projects.factories.RemoteClientCreatorFactory;
import io.toro.pairprogramming.services.storages.LocalProjectStorage;
import io.toro.pairprogramming.services.storages.ProjectStorageService;
import org.apache.commons.beanutils.BeanUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
public class ProjectManagerTest {

    private ProjectManager projectManager;

    @MockBean
    private ProjectRepository projectRepository;

    @MockBean
    private ProjectStorageService projectStorageService;

    @MockBean
    private RemoteClientCreatorFactory creatorFactory;

    @Before
    public void setUp() throws Exception {
        projectManager = new ProjectManager(projectRepository, projectStorageService, creatorFactory);
    }

    @Test
    public void shouldGetProjectById() throws Exception {
        Project project = ProjectUtils.createProject();

        when(projectRepository.findOne(project.getIds()))
                .thenReturn(project);

        Project actual = projectManager.getById(project.getIds());

        assertEquals(project.getName(), actual.getName());
    }

    @Test
    public void shouldGetByUser() throws Exception {
        User user = AuthUtils.createUser();

        List<Project> projects = ProjectUtils.createProjects();

        projects.forEach(p -> p.setUser(user));

        // Java passes objects by reference. We need to clone.
        List<Project> projectsClone = new ArrayList<>(projects);

        when(projectRepository.findByUserId(user.getIds()))
                .thenReturn(projectsClone);

        List<Project> actual = projectManager.getByUser(user);

        assertTrue(actual.containsAll(projects));
    }

    @Test
    public void shouldCreateProject() throws Exception {
        User user = AuthUtils.createUser();
        user.setId(Long.parseLong("1"));

        Project project = ProjectUtils.createProject();
        project.setUser(user);

        Project projectClone = new Project();
        BeanUtils.copyProperties(projectClone, project);

        // Simulate db adding id to saved entity
        projectClone.setId(Long.parseLong("1"));

        when(projectRepository.save(project))
                .thenReturn(projectClone);

        Project actual = projectManager.createProject(project);
        assertEquals(actual.getName(), projectClone.getName());

        verify(projectStorageService, times(1))
                .createDirectory(projectClone, "/");
    }

    @Test
    public void shouldImportProject() throws Exception {
        User user = AuthUtils.createUser();
        user.setId(Long.parseLong("1"));

        Project project = ProjectUtils.createProject();

        project.setUser(user);
        project.setRepoName("repo-name");

        when(projectRepository.findOne(project.getIds()))
                .thenReturn(project);

        RemoteClientCreator creator = mock(RemoteClientCreator.class);

        when(creatorFactory.getCreator(project.getType()))
                .thenReturn(creator);

        RemoteRepoClient client = mock(RemoteRepoClient.class);

        when(creator.createClient(user.getEmail()))
                .thenReturn(client);

        projectManager.importProject(project.getIds(), project.getType(), project.getRepoName());

        File filePath = new LocalProjectStorage().getProjectDirectory(project).toFile();

        verify(client, times(1))
                .cloneRepo(project.getRepoName(), filePath);

        verify(projectRepository, times(1))
                .save(project);
    }

    @Test
    public void shouldUpdateProject() throws Exception {
        User user = AuthUtils.createUser();
        user.setId(Long.parseLong("1"));

        Project project = ProjectUtils.createProject();
        project.setId(Long.parseLong("1"));
        project.setUser(user);

        Project updateProject = ProjectUtils.createProject();

        when(projectRepository.exists(project.getIds()))
                .thenReturn(true);

        when(projectRepository.findOne(project.getIds()))
                .thenReturn(project);

        // Java passes objects by reference. We need to clone.
        Project expected = (Project) BeanUtils.cloneBean(updateProject);

        expected.setId(project.getIds());
        expected.setUser(project.getUser());
        expected.setType(project.getType());

        when(projectRepository.save(updateProject))
                .thenReturn(expected);

        Project actual = projectManager.updateProject(project.getIds(), updateProject);

        assertEquals(actual.getName(), expected.getName());

        verify(projectRepository, times(1))
                .save(updateProject);
    }

    @Test
    public void shouldDeleteProject() throws Exception {
        User user = AuthUtils.createUser();
        user.setId(Long.parseLong("1"));

        Project project = ProjectUtils.createProject();
        project.setId(Long.parseLong("1"));
        project.setUser(user);

        List<Project> userProjects = new ArrayList<>(Arrays.asList(project));

        user.setProjects(userProjects);

        when(projectRepository.findOne(project.getIds()))
                .thenReturn(project);

        projectManager.deleteProject(project.getIds());

        verify(projectRepository, times(1))
                .delete(project.getIds());

        verify(projectStorageService, times(1))
                .deleteProject(project);
    }
}
