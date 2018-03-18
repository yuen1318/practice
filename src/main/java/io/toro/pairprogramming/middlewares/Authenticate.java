package io.toro.pairprogramming.middlewares;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.toro.pairprogramming.handlers.auth.NotAuthorizedException;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import io.toro.pairprogramming.services.AuthService;

public class Authenticate extends HandlerInterceptorAdapter {

    private AuthService authService;

    public Authenticate(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
        String requestMethod = request.getMethod();

        Optional<String> header = Optional.ofNullable(request.getHeader("Authorization"));

        authenticate(header);

        return true;
    }

    private void authenticate(Optional<String> header) {
        if(
                !header.isPresent() ||
                !hasToken(header.get()) ||
                !authService.verifyToken(authService.getTokenFromHeader(header.get()))
        ) {
            throw new NotAuthorizedException("Not authorized");
        }
    }

    private Boolean hasToken(String header) {
        return header.startsWith("Bearer ");
    }

    private Boolean isValidToken(String token) {
        return authService.verifyToken(token);
    }

    private Boolean isPostMethod(String method) {
        return method.equals(HttpMethod.POST.toString());
    }

    private Boolean isPutMethod(String method) {
        return method.equals(HttpMethod.PUT.toString());
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
