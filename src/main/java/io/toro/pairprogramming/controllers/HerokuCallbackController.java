package io.toro.pairprogramming.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;

import io.toro.pairprogramming.controllers.api.ProjectController;
import io.toro.pairprogramming.models.oauth.states.ProjectState;
import io.toro.pairprogramming.models.Project;
import io.toro.pairprogramming.models.oauth.states.UpdateProjectState;
import io.toro.pairprogramming.services.projects.clients.HerokuClient;
import io.toro.pairprogramming.services.projects.ProjectManager;
import io.toro.pairprogramming.services.projects.clients.RemoteRepoClient;
import io.toro.pairprogramming.services.projects.providers.heroku.HerokuProvider;
import io.toro.pairprogramming.services.storages.LocalProjectStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.net.URI;
import java.util.Base64;

@Controller
public class HerokuCallbackController {

    private ObjectMapper mapper = new ObjectMapper();

    private ProjectManager projectManager;

    private HerokuProvider provider;

    @Autowired
    HerokuCallbackController(ProjectManager projectManager, HerokuProvider provider) {
        this.projectManager = projectManager;
        this.provider = provider;
    }

    @GetMapping("/oauth/heroku/callback")
    public ResponseEntity callback(HttpServletRequest request) throws Exception {
        ProjectState state = parseState(decodeState(request.getParameter("state")));

        Project project = projectManager.getById(state.getProjectId());

        TokenResponse token = provider
                .newTokenRequest(request.getParameter("code"))
                .execute();

        Credential credential = provider.createAndStoreCredential(token, state.getId());

        HerokuClient service = new HerokuClient(credential.getAccessToken());

        continueProcess(service, state, project);

        URI projectUri = URI.create( ProjectController.baseUrl + "/" + state.getProjectId());

        return ResponseEntity.status(HttpStatus.FOUND).location(projectUri).build();
    }

    private void continueProcess(RemoteRepoClient service, ProjectState state, Project project) throws Exception {
        switch (state.getProcess()) {
            case CREATE:
                service.createRepo(project.getRepoName());
                break;

            case IMPORT:
                // See ProjectManager line #74
                File path = new LocalProjectStorage().getProjectDirectory(project).toFile();

                service.cloneRepo(project.getRepoName(), path);
                break;

            case UPDATE:
                UpdateProjectState updateState = (UpdateProjectState) state;

                String oldRepoName = updateState.getOldRepoName();

                service.renameRepo(oldRepoName, project.getRepoName());
                break;
        }
    }

    private String decodeState(String state) {
        return new String(Base64.getDecoder().decode(state));
    }

    private ProjectState parseState(String state) throws Exception {
        return mapper.readValue(state, ProjectState.class);
    }
}
