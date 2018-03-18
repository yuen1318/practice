package io.toro.pairprogramming.services.projects.providers.github;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import org.springframework.stereotype.Component;

@Component
public class GithubProvider extends AuthorizationCodeFlow {

    public GithubProvider(Builder builder) {
        super(builder);
    }
}
