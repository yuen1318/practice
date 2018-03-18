package io.toro.pairprogramming.services.projects.providers.heroku;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import org.springframework.stereotype.Component;

@Component
public class HerokuProvider extends AuthorizationCodeFlow {

    public HerokuProvider(Builder builder) {
        super(builder);
    }
}
