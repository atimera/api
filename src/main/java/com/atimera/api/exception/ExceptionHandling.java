package com.atimera.api.exception;

import com.atimera.api.dto.HttpCustomResponse;
import com.atimera.api.exception.domain.EmailExistsException;
import com.atimera.api.exception.domain.EmailNotFoundException;
import com.atimera.api.exception.domain.UserNotFoundException;
import com.atimera.api.exception.domain.UsernameExistsException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.NoResultException;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.Objects;

@RestControllerAdvice
@Primary
@Slf4j
public class ExceptionHandling implements ErrorController {
    public static final String ERROR_PATH = "/error";
    private static final String ACCOUNT_LOCKED = "Votre compte a été bloqué. Veuillez contacter l'administrateur";
    private static final String METHOD_IS_NOT_ALLOWED = "Request Method non autorisée sur cet endpoint. Envoyer une '%s' request";
    private static final String INTERNAL_SERVER_ERROR_MSG = "Erreur survenue lors du traitement de la requête";
    private static final String INCORRECT_CREDENTIALS = "L'identifiant ou le mot de passe est incorrect. Veuillez réessayer";
    private static final String ACCOUNT_DISABLED = "Votre compte a été désactivé. Veuillez contacter votre administrateur";
    private static final String ERROR_PROCESSING_FILE = "Erreur survenue lors du traitement du fichier";
    private static final String NOT_ENOUGH_PERMISSION = "Vous n'avez assez de droit";

    @ExceptionHandler(DisabledException.class) // Compte désactivé
    public ResponseEntity<HttpCustomResponse> accountDisabledException() {
        return createHttpResponse(HttpStatus.BAD_REQUEST, ACCOUNT_DISABLED);
    }

    @ExceptionHandler(BadCredentialsException.class) // identifiants incorrects
    public ResponseEntity<HttpCustomResponse> badCredentialsException() {
        return createHttpResponse(HttpStatus.BAD_REQUEST, INCORRECT_CREDENTIALS);
    }

    @ExceptionHandler(AccessDeniedException.class) // accès refusé
    public ResponseEntity<HttpCustomResponse> accessDeniedException() {
        return createHttpResponse(HttpStatus.FORBIDDEN, NOT_ENOUGH_PERMISSION);
    }

    @ExceptionHandler(LockedException.class) // Compte bloqué
    public ResponseEntity<HttpCustomResponse> LockedException() {
        return createHttpResponse(HttpStatus.UNAUTHORIZED, ACCOUNT_LOCKED);
    }

    @ExceptionHandler(TokenExpiredException.class) // token expiré - Auth0
    public ResponseEntity<HttpCustomResponse> tokenExpiredException(TokenExpiredException ex) {
        return createHttpResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(EmailExistsException.class) // Email déjà utilisé
    public ResponseEntity<HttpCustomResponse> emailExistsException(EmailExistsException ex) {
        return createHttpResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(UsernameExistsException.class) // Email déjà utilisé
    public ResponseEntity<HttpCustomResponse> usernameExistsException(UsernameExistsException ex) {
        return createHttpResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(EmailNotFoundException.class) // Email non trouvée
    public ResponseEntity<HttpCustomResponse> emailNotFoundException(EmailNotFoundException ex) {
        return createHttpResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class) // User non trouvée
    public ResponseEntity<HttpCustomResponse> userNotFoundException(UserNotFoundException ex) {
        return createHttpResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class) // method Http non supportée
    public ResponseEntity<HttpCustomResponse> methodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        HttpMethod httpMethod = Objects.requireNonNull(ex.getSupportedHttpMethods()).iterator().next();
        return createHttpResponse(HttpStatus.BAD_REQUEST, String.format(METHOD_IS_NOT_ALLOWED, httpMethod));
    }

    @ExceptionHandler(Exception.class) // Erreur interne
    public ResponseEntity<HttpCustomResponse> internalServerErrorException(Exception ex) {
        log.error(ex.getMessage());
        return createHttpResponse(HttpStatus.INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR_MSG);
    }

    @ExceptionHandler(NoResultException.class) // Ressource non trouvée
    public ResponseEntity<HttpCustomResponse> notFoundException(Exception ex) {
        log.error(ex.getMessage());
        return createHttpResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(IOException.class) // Erreurs concernant fichiers
    public ResponseEntity<HttpCustomResponse> iOException(Exception ex) {
        log.error(ex.getMessage());
        return createHttpResponse(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_PROCESSING_FILE);
    }

    @RequestMapping(ERROR_PATH) // page non trouvée
    public ResponseEntity<HttpCustomResponse> notFound404() {
        return createHttpResponse(HttpStatus.NOT_FOUND, "Il n'y a aucune ressource sur cet URL");
    }

    private ResponseEntity<HttpCustomResponse> createHttpResponse(HttpStatus httpStatus, String message) {
        HttpCustomResponse httpCustomResponse = HttpCustomResponse.builder()
                .httpStatus(httpStatus)
                .httpStatusCode(httpStatus.value())
                .reason(httpStatus.getReasonPhrase().toUpperCase())
                .message(message)
                .build();
        // Constructeur: @Nullable T body, HttpStatus status
        return new ResponseEntity<>(httpCustomResponse, httpStatus);
    }


}
