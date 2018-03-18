package io.toro.pairprogramming.middlewares;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import io.toro.pairprogramming.services.AuthService;

@Configuration
public class Middleware extends WebMvcConfigurerAdapter {

    @Autowired
    private AuthService authService;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new Authenticate(authService))
                .addPathPatterns("/api/v1/**")
                .excludePathPatterns("/api/v1/auth/login", "/api/v1/users/register", "/api/v1/projects/upload");
    }
}
