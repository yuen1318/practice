package io.toro.pairprogramming.config;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow.Builder;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.MemoryDataStoreFactory;
import io.toro.pairprogramming.services.projects.factories.GithubClientCreator;
import io.toro.pairprogramming.services.projects.factories.HerokuClientCreator;
import io.toro.pairprogramming.services.projects.factories.RemoteClientCreatorFactory;
import io.toro.pairprogramming.services.projects.providers.MyClientParametersAuthentication;
import io.toro.pairprogramming.services.projects.providers.github.GithubCredentials;
import io.toro.pairprogramming.services.projects.providers.github.GithubProvider;
import io.toro.pairprogramming.services.projects.providers.heroku.HerokuCredentials;
import io.toro.pairprogramming.services.projects.providers.heroku.HerokuProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.Arrays;

import static io.toro.pairprogramming.models.request.ProjectType.GITHUB;
import static io.toro.pairprogramming.models.request.ProjectType.HEROKU;

@Configuration
public class RemoteClientCreatorFactoryConfig {

    @Bean
    public RemoteClientCreatorFactory remoteClientCreatorFactory(
        HerokuProvider herokuProvider,
        GithubProvider githubProvider
    ) {
        RemoteClientCreatorFactory factory = new RemoteClientCreatorFactory();

        factory.addCreator(HEROKU, new HerokuClientCreator(herokuProvider));
        factory.addCreator(GITHUB, new GithubClientCreator(githubProvider));

        return factory;
    }

    @Bean
    public GithubProvider githubProvider(HttpTransport httpTransport, JsonFactory jsonFactory) {
        String tokenServerUrl = "https://github.com/login/oauth/access_token";

        String authorizationServerUrl = "https://github.com/login/oauth/authorize";

        String[] scopes = {"repo","read:user"};

        Builder builder = new Builder(
                BearerToken.authorizationHeaderAccessMethod(),
                httpTransport,
                jsonFactory,
                new GenericUrl(tokenServerUrl),
                new MyClientParametersAuthentication(GithubCredentials.CLIENT_ID, GithubCredentials.CLIENT_SECRET),
                GithubCredentials.CLIENT_ID,
                authorizationServerUrl
        );

        builder.setScopes(Arrays.asList(scopes));

        try {
            builder.setDataStoreFactory(new MemoryDataStoreFactory());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new GithubProvider(builder);
    }

    @Bean
    public HerokuProvider herokuProvider(HttpTransport httpTransport, JsonFactory jsonFactory) {
        String tokenServerUrl = "https://id.heroku.com/oauth/token";

        String authorizationServerUrl = "https://id.heroku.com/oauth/authorize";

        String[] scopes = {"read","write"};

        Builder builder = new Builder(
                BearerToken.authorizationHeaderAccessMethod(),
                httpTransport,
                jsonFactory,
                new GenericUrl(tokenServerUrl),
                new MyClientParametersAuthentication(HerokuCredentials.CLIENT_ID, HerokuCredentials.CLIENT_SECRET),
                HerokuCredentials.CLIENT_ID,
                authorizationServerUrl
        );

        builder.setScopes(Arrays.asList(scopes));

        try {
            builder.setDataStoreFactory(new MemoryDataStoreFactory());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new HerokuProvider(builder);
    }

    @Bean
    public HttpTransport httpTransport() {
        return new NetHttpTransport();
    }

    @Bean
    public JsonFactory jsonFactory() {
        return new JacksonFactory();
    }
}
