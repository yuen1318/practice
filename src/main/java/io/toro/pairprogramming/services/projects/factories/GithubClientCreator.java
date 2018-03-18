package io.toro.pairprogramming.services.projects.factories;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.Credential;
import io.toro.pairprogramming.handlers.ClientNotAuthorizedException;
import io.toro.pairprogramming.services.projects.clients.GithubClient;
import io.toro.pairprogramming.services.projects.clients.RemoteRepoClient;
import io.toro.pairprogramming.services.projects.providers.github.GithubProvider;

import java.io.IOException;

public class GithubClientCreator implements RemoteClientCreator {

    private GithubProvider provider;

    public GithubClientCreator(GithubProvider provider) {
        this.provider = provider;
    }

    @Override
    public RemoteRepoClient createClient(String userId) throws ClientNotAuthorizedException {
        String apiKey = "";

        try {
            Credential credential = provider.loadCredential(userId);

            if (credential == null) {
                ClientNotAuthorizedException e = new ClientNotAuthorizedException();

                e.setAuthorizationUrl(getAuthorizationUrl());

                throw e;
            }

            apiKey = credential.getAccessToken();
        } catch (IOException e) {
            // This should not happen because it is handled in
            e.printStackTrace();
        }

        return new GithubClient(apiKey);
    }

    @Override
    public AuthorizationCodeFlow getProvider() {
        return provider;
    }

    private AuthorizationCodeRequestUrl getAuthorizationUrl() {
        return provider.newAuthorizationUrl();
    }
}
