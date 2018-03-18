package io.toro.pairprogramming.handlers;

import java.util.Map;

import io.toro.pairprogramming.handlers.invitation.InvitationIsAlreadyExistingException;
import io.toro.pairprogramming.handlers.invitation.InvitationNotFoundException;
import io.toro.pairprogramming.handlers.invitation.ProjectNotFoundException;
import org.json.HTTP;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import io.jsonwebtoken.SignatureException;
import io.toro.pairprogramming.builders.ApiErrorBuilder;
import io.toro.pairprogramming.handlers.auth.EmailAlreadyExistException;
import io.toro.pairprogramming.handlers.auth.FailedAuthenticationException;
import io.toro.pairprogramming.handlers.auth.ForbiddenActionException;
import io.toro.pairprogramming.handlers.auth.NotAuthorizedException;
import io.toro.pairprogramming.handlers.project.InvalidInputException;
import io.toro.pairprogramming.handlers.project.ProjectAlreadyExistException;
import io.toro.pairprogramming.handlers.project.ProjectEmptyException;
import io.toro.pairprogramming.handlers.workshift.WorkShiftAlreadyExistException;

@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler({NotAuthorizedException.class, FailedAuthenticationException.class})
    public ResponseEntity<?> unauthorizedHandler(RuntimeException ex) {
        Map<String, Object> apiErrorBuilder = (new ApiErrorBuilder()).addCode(HttpStatus.UNAUTHORIZED.value()).addMessage(ex.getMessage()).get();
        return new ResponseEntity<Object>(apiErrorBuilder, new HttpHeaders(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({
            EmailAlreadyExistException.class,
            WorkShiftAlreadyExistException.class
    })
    public ResponseEntity<?> conflictHandler(RuntimeException ex) {
        Map<String, Object> apiErrorBuilder = (new ApiErrorBuilder()).addCode(HttpStatus.CONFLICT.value()).addMessage(ex.getMessage()).get();
        return new ResponseEntity<Object>(apiErrorBuilder, new HttpHeaders(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = {
            ProjectAlreadyExistException.class,
            IllegalArgumentException.class,
            HttpMessageNotReadableException.class
    })
    public ResponseEntity badRequestHandler(RuntimeException ex) {
        Map<String, Object> apiErrorBuilder = ( new ApiErrorBuilder() ).addCode( 400 ).addMessage(
                ex.getMessage() ).get();
        return ResponseEntity.badRequest().body( apiErrorBuilder );
    }

    @ExceptionHandler(value = {ForbiddenActionException.class})
    public ResponseEntity forbiddenHandler(RuntimeException ex) {
        Map<String, Object> apiErrorBuilder = (new ApiErrorBuilder()).addCode(HttpStatus.FORBIDDEN.value()).addMessage(ex.getMessage()).get();
        return new ResponseEntity<Object>(apiErrorBuilder, new HttpHeaders(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = {ProjectNotFoundException.class})
    public ResponseEntity projectNotFoundHandler(RuntimeException ex){
        Map<String, Object> apiErrorBuilder = (new ApiErrorBuilder()).addCode(HttpStatus.NOT_FOUND.value()).addMessage(ex.getMessage()).get();
        return new ResponseEntity<Object>(apiErrorBuilder, new HttpHeaders(), HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(value = {InvitationNotFoundException.class})
    public ResponseEntity invitationNotFoundHandler(RuntimeException ex){
        Map<String, Object> apiErrorBuilder = (new ApiErrorBuilder()).addCode(HttpStatus.NOT_FOUND.value()).addMessage(ex.getMessage()).get();
        return new ResponseEntity<Object>(apiErrorBuilder, new HttpHeaders(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {InvitationIsAlreadyExistingException.class})
    public ResponseEntity invitationAlreadyExistsHandler(RuntimeException ex){
        Map<String, Object> apiErrorBuilder = (new ApiErrorBuilder()).addCode(HttpStatus.UNPROCESSABLE_ENTITY.value()).addMessage(ex.getMessage()).get();
        return new ResponseEntity<Object>(apiErrorBuilder, new HttpHeaders(), HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(value = {
            ProjectEmptyException.class,
            NullPointerException.class
    })
    public ResponseEntity notFoundHandler(RuntimeException ex) {
        Map<String, Object> apiErrorBuilder = (new ApiErrorBuilder()).addCode(404).addMessage(ex.getMessage()).get();
        return new ResponseEntity<Object>(apiErrorBuilder, new HttpHeaders(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {InvalidInputException.class})
    public ResponseEntity unprocessableEntityHandler(RuntimeException ex) {
        Map<String, Object> apiErrorBuilder = (new ApiErrorBuilder()).addCode(402).addMessage(ex.getMessage()).get();
        return new ResponseEntity<Object>(apiErrorBuilder, new HttpHeaders(), HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<?> handleSignatureException(SignatureException ex) {
        Map<String, Object> apiErrorBuilder = (new ApiErrorBuilder()).addCode(401).addMessage("Failed authentication").get();
        return new ResponseEntity<Object>(apiErrorBuilder, new HttpHeaders(), HttpStatus.UNAUTHORIZED);
    }
}
