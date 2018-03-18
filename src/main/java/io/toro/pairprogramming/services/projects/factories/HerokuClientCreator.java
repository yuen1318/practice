package io.toro.pairprogramming.services.projects.factories;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.Credential;
import io.toro.pairprogramming.handlers.ClientNotAuthorizedException;
import io.toro.pairprogramming.services.projects.clients.HerokuClient;
import io.toro.pairprogramming.services.projects.clients.RemoteRepoClient;
import io.toro.pairprogramming.services.projects.providers.heroku.HerokuProvider;

import java.io.IOException;

public class HerokuClientCreator implements RemoteClientCreator {

    private HerokuProvider provider;

    public HerokuClientCreator(HerokuProvider provider) {
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
            e.printStackTrace();
        }

        return new HerokuClient(apiKey);
    }

    @Override
    public AuthorizationCodeFlow getProvider() {
        return provider;
    }

    private AuthorizationCodeRequestUrl getAuthorizationUrl() {
        return getProvider().newAuthorizationUrl();
    }
}

