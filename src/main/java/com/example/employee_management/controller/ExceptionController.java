package com.example.employee_management.controller;

import com.example.employee_management.exceptions.EmployeeNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Slf4j
public class ExceptionController extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {EmployeeNotFoundException.class, IllegalArgumentException.class})
    public ResponseEntity<Object> employeeNotFoundException(RuntimeException ex, WebRequest request){
        logException(ex);
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);
        headers.setCacheControl(CacheControl.noCache().mustRevalidate());

        return createResponseEntity(pd, headers, HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(value = {UsernameNotFoundException.class})
    public ResponseEntity<Object> UsernameNotFoundException(Exception ex, WebRequest request){
        logException(ex);
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);
        headers.setCacheControl(CacheControl.noCache().mustRevalidate());

        return createResponseEntity(pd, headers, HttpStatus.UNAUTHORIZED, request);
    }

    private void logException(Exception ex) {
        log.error("Caught Exception", ex);
    }
}

