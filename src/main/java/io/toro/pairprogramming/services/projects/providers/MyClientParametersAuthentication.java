package io.toro.pairprogramming.services.projects.providers;

import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import org.springframework.http.MediaType;

import java.io.IOException;

public class MyClientParametersAuthentication extends ClientParametersAuthentication {

    public MyClientParametersAuthentication(String clientId, String clientSecret) {
        super(clientId, clientSecret);
    }

    @Override
    public void intercept(HttpRequest request) throws IOException {
        super.intercept(request);

        HttpHeaders header = new HttpHeaders();

        header.setAccept(MediaType.APPLICATION_JSON_VALUE);

        request.setHeaders(header);
    }
}
