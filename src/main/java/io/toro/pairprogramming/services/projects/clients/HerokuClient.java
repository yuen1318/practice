package io.toro.pairprogramming.services.projects.clients;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.http.HttpStatus;

import java.io.File;

public class HerokuClient implements RemoteRepoClient {

    private static String apiServerUrl = "https://api.heroku.com";

    private String apiKey;

    public HerokuClient(String apiKey) {
        this.apiKey = apiKey;

        Unirest.setDefaultHeader("Accept", "application/vnd.heroku+json; version=3");
        Unirest.setDefaultHeader("Authorization", "Bearer " + apiKey);
    }

    public void createRepo(String repoName) throws Exception {
        HttpResponse<JsonNode> response = Unirest.post(apiServerUrl + "/apps")
                .field("name", repoName)
                .asJson();

        if (response.getStatus() != HttpStatus.CREATED.value()) {
            throw new Exception(response.getBody().toString());
        }
    }

    public void cloneRepo(String repoName, File clonePath) throws Exception {
        Git.cloneRepository()
            .setURI(getRepoUrl(repoName))
            .setDirectory(clonePath)
            .setCredentialsProvider(new UsernamePasswordCredentialsProvider("", apiKey))
            .call();
    }

    public void renameRepo(String oldRepoName, String newRepoName) throws Exception {
        HttpResponse<JsonNode> response = Unirest.patch(apiServerUrl + "/apps/" + oldRepoName)
                .field("name", newRepoName)
                .asJson();

        if (response.getStatus() != HttpStatus.OK.value()) {
            throw new Exception(response.getBody().toString());
        }
    }

    public String getRepoUrl(String repoName) throws Exception {
        HttpResponse<JsonNode> response = Unirest.get(apiServerUrl + "/apps/" + repoName)
                .asJson();

        if (response.getStatus() == HttpStatus.NOT_FOUND.value()) {
            throw new Exception("Heroku app not found");
        }

        if (response.getStatus() != HttpStatus.OK.value()) {
            throw new Exception(response.getBody().toString());
        }

        return response.getBody().getObject().getString("git_url");
    }
}
