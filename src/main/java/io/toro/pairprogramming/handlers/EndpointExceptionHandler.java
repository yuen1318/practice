package io.toro.pairprogramming.handlers;

import io.toro.pairprogramming.builders.ApiErrorBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class EndpointExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        Map<String, Object> apiErrorBuilder = (new ApiErrorBuilder()).addCode(HttpStatus.METHOD_NOT_ALLOWED.value()).addMessage(ex.getMessage()).get();
        return new ResponseEntity<Object>(apiErrorBuilder, new HttpHeaders(), HttpStatus.METHOD_NOT_ALLOWED);
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        Map<String, Object> apiErrorBuilder = (new ApiErrorBuilder()).addCode(HttpStatus.NOT_FOUND.value()).addMessage(ex.getMessage()).get();
        return new ResponseEntity<Object>(apiErrorBuilder, new HttpHeaders(), HttpStatus.NOT_FOUND);
    }
}
