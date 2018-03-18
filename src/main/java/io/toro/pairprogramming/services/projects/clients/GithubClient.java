package io.toro.pairprogramming.services.projects.clients;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;

import java.io.File;

public class GithubClient implements RemoteRepoClient {

    private final static String API_SERVER_URL = "https://api.github.com";

    private String apiKey;

    public GithubClient(String apiKey) {
        this.apiKey = apiKey;

        Unirest.setDefaultHeader("Accept", "application/vnd.github.v3+json");

        Unirest.setDefaultHeader("Authorization", "token " + apiKey);
    }

    public void createRepo(String repoName) throws Exception {
        JSONObject requestBody = new JSONObject();

        requestBody.put("name", repoName);

        HttpResponse<JsonNode> response = Unirest.post(API_SERVER_URL + "/user/repos")
                .body(requestBody)
                .asJson();

        if (response.getStatus() != HttpStatus.CREATED.value()) {
            throw new Exception(response.getBody().toString());
        }
    }

    public void cloneRepo(String repoName, File clonePath) throws Exception {
        Git.cloneRepository()
                .setURI(getRepoUrl(repoName))
                .setCredentialsProvider(new UsernamePasswordCredentialsProvider(apiKey, ""))
                .setDirectory(clonePath)
                .call();
    }

    public void renameRepo(String oldRepoName, String newRepoName) throws Exception {
        String url = String.join("/", "repos", getRepoOwner(), oldRepoName);

        HttpResponse<JsonNode> response = Unirest.patch(url)
                .header("Authorization", "token " + apiKey)
                .field("name", newRepoName)
                .asJson();

        if (response.getStatus() != HttpStatus.OK.value()) {
            throw new Exception(response.getBody().toString());
        }
    }

    public String getRepoUrl(String repoName) throws Exception {
        HttpResponse<JsonNode> response = Unirest.get(API_SERVER_URL + "/repos/" + getRepoOwner() + repoName)
                .header("Authorization", apiKey)
                .asJson();

        if (response.getStatus() != HttpStatus.OK.value()) {
            throw new Exception(response.getBody().toString());
        }

        return response.getBody().getObject().getString("clone_url");
    }

    private String getRepoOwner() throws Exception {
        HttpResponse<JsonNode> response = Unirest.get(API_SERVER_URL + "/user")
                .header("Authorization", "token " + apiKey)
                .asJson();

        if (response.getStatus() != HttpStatus.OK.value()) {
            throw new Exception(response.getBody().toString());
        }

        return response.getBody().getObject().getString("login");
    }
}
