package io.toro.pairprogramming.services.projects.clients;

import java.io.File;

public interface RemoteRepoClient {

    void createRepo(String repoName) throws Exception;

    void cloneRepo(String repoName, File clonePath) throws Exception;

    void renameRepo(String oldRepoName, String newRepoName) throws Exception;

    String getRepoUrl(String repoName) throws Exception;
}
