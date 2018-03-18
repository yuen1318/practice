package io.toro.pairprogramming.services.projects.factories;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import io.toro.pairprogramming.handlers.ClientNotAuthorizedException;
import io.toro.pairprogramming.services.projects.clients.RemoteRepoClient;

public interface RemoteClientCreator {
    RemoteRepoClient createClient(String userId) throws ClientNotAuthorizedException;

    AuthorizationCodeFlow getProvider();
}
