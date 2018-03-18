package io.toro.pairprogramming.handlers;

import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;

public class ClientNotAuthorizedException extends Exception {

    private AuthorizationCodeRequestUrl authorizationUrl;

    public AuthorizationCodeRequestUrl getAuthorizationUrl() {
        return authorizationUrl;
    }

    public void setAuthorizationUrl(AuthorizationCodeRequestUrl authorizationUrl) {
        this.authorizationUrl = authorizationUrl;
    }
}
