package io.toro.pairprogramming.services.projects;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.toro.pairprogramming.handlers.ClientNotAuthorizedException;
import io.toro.pairprogramming.handlers.ProjectNotFoundException;
import io.toro.pairprogramming.models.Project;
import io.toro.pairprogramming.models.request.ProjectType;
import io.toro.pairprogramming.models.User;
import io.toro.pairprogramming.models.oauth.states.ProjectState;
import io.toro.pairprogramming.repositories.ProjectRepository;
import io.toro.pairprogramming.services.projects.clients.RemoteRepoClient;
import io.toro.pairprogramming.services.projects.factories.RemoteClientCreatorFactory;
import io.toro.pairprogramming.services.storages.LocalProjectStorage;
import io.toro.pairprogramming.services.storages.ProjectStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Base64;
import java.util.List;

import static io.toro.pairprogramming.models.oauth.states.ProjectState.Process.IMPORT;

@Component
public class ProjectManager {

    private ObjectMapper mapper = new ObjectMapper();

    private ProjectRepository projectRepository;

    private ProjectStorageService projectStorageService;

    private RemoteClientCreatorFactory creatorFactory;

    @Autowired
    public ProjectManager(ProjectRepository projectRepository, ProjectStorageService projectStorageService, RemoteClientCreatorFactory creatorFactory) {
        this.projectRepository = projectRepository;

        this.projectStorageService = projectStorageService;

        this.creatorFactory = creatorFactory;
    }

    public Project getById(Long id) {
        return projectRepository.findOne(id);
    }

    public List<Project> getByUser(User user) {
        return projectRepository.findByUserId(user.getIds());
    }

    public Project createProject(Project project) throws Exception {
        // Project will only have repo name after import.
        project.setRepoName(null);

        project = projectRepository.save(project);

        projectStorageService.createDirectory(project, "/");

        return project;
    }

    public void importProject(Long id, ProjectType type, String repoName) throws Exception {
        Project project = projectRepository.findOne(id);

        try {
            project.setRepoName(repoName);

            projectRepository.save(project);

            RemoteRepoClient client = creatorFactory
                    .getCreator(type)
                    .createClient(project.getUser().getEmail());

            // Not sure about this.
            // Clone needs File Path, if we used remote storage in the future
            // will it have a File path?
            File projectDir = new LocalProjectStorage()
                                .getProjectDirectory(project)
                                .toFile();

            client.cloneRepo(repoName, projectDir);
        } catch (ClientNotAuthorizedException e) {
            ProjectState state = createState(project, IMPORT);

            e.getAuthorizationUrl().setState(convertStateToBase64(state));

            throw e;
        }
    }

    public Project updateProject(Long id, Project project) throws Exception {
        if ( ! projectRepository.exists(id) ) {
            throw new ProjectNotFoundException("Project with id: " + id + " does not exists.");
        }

        Project oldProject = projectRepository.findOne(id);

        oldProject.setName(project.getName());

        projectRepository.save(oldProject);

        projectStorageService.moveProject(oldProject, project);

        return oldProject;
    }

    public void deleteProject(Long id) {
        Project project = projectRepository.findOne(id);

        project.getUser().getProjects().removeIf(p -> p.getIds().equals(id));

        projectRepository.delete(id);

        projectStorageService.deleteProject(project);
    }

    private ProjectState createState(Project project, ProjectState.Process process) {
        ProjectState state = new ProjectState();

        state.setId(project.getUser().getEmail());

        state.setProcess(process);

        state.setProjectId(project.getIds());

        return state;
    }

    private String convertStateToBase64(ProjectState state) {
        String base64State = "";

        try {
            byte[] jsonState = mapper.writeValueAsBytes(state);

            base64State = Base64.getEncoder().encodeToString(jsonState);
        } catch(JsonProcessingException e) {
            // This should not happen because we are writing valid json
            e.printStackTrace();
        }

        return base64State;
    }

    public List<Project> match(Long id){
        return projectRepository.findByUserSkills( id );
    }
}
