package com.cosmo.wanda_web.controller.handlers;

import com.cosmo.wanda_web.dto.errors.CustomError;
import com.cosmo.wanda_web.dto.errors.ValidationError;
import com.cosmo.wanda_web.services.exceptions.*;
import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class ControllerExceptionsHandler {

    private static final Logger log = LoggerFactory.getLogger(ControllerExceptionsHandler.class);

    @ExceptionHandler(InvalidFunctionException.class)
    public ResponseEntity<CustomError> InvalidFunction(InvalidFunctionException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        CustomError err = new CustomError(Instant.now(), status.value(), e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<CustomError> FeignException(FeignException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        CustomError err = new CustomError(Instant.now(), status.value(), "tive um probleminha ao tentar processar sua função, peço desculpas por isso, tente novamente!", request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }


    @ExceptionHandler(ChallengeException.class)
    public ResponseEntity<CustomError> challengeException(ChallengeException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        CustomError err = new CustomError(Instant.now(), status.value(), e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<CustomError> Database(DatabaseException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        CustomError err = new CustomError(Instant.now(), status.value(), e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }


    @ExceptionHandler(RegisterException.class)
    public ResponseEntity<CustomError> RegisterException(RegisterException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        CustomError err = new CustomError(Instant.now(), status.value(), e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<CustomError> resourceNotFound(ResourceNotFoundException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        CustomError err = new CustomError(Instant.now(), status.value(), e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(TournamentException.class)
    public ResponseEntity<CustomError> Tournament(TournamentException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        CustomError err = new CustomError(Instant.now(), status.value(), e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomError> methodArgumentNotValidation(MethodArgumentNotValidException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
        ValidationError err = new ValidationError(Instant.now(), status.value(), "Dados inválidos", request.getRequestURI());
        for (FieldError f : e.getBindingResult().getFieldErrors()){
            err.addError(f.getField(), f.getDefaultMessage());
        }
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<CustomError> badCredentials(BadCredentialsException e, HttpServletRequest request) {
        var status = HttpStatus.UNAUTHORIZED;
        var body = new CustomError(Instant.now(), status.value(), "Credenciais inválidas", request.getRequestURI());
        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomError> unexpectedException(Exception e, HttpServletRequest request) {
        log.error("Exceção não mapeada. uri={}, erro={}", request.getRequestURI(), e.getMessage(), e);
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        CustomError err = new CustomError(Instant.now(), status.value(),
                "Ocorreu um erro inesperado. Tente novamente.", request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

}
