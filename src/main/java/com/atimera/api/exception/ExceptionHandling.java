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
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.persistence.NoResultException;
import java.io.IOException;
import java.util.Objects;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
@Primary
@Slf4j
public class ExceptionHandling implements ErrorController {
    public static final String ERROR_PATH = "/error";
    public static final String NO_RESOURCE_FOUND = "Il n'y a aucune ressource sur cet URL";
    private static final String ACCOUNT_LOCKED = "Votre compte a été bloqué. Veuillez contacter l'administrateur";
    private static final String METHOD_IS_NOT_ALLOWED = "Request Method non autorisée sur cet endpoint. Envoyer une '%s' request";
    private static final String INTERNAL_SERVER_ERROR_MSG = "Erreur survenue lors du traitement de la requête";
    private static final String INCORRECT_CREDENTIALS = "L'identifiant ou le mot de passe est incorrect. Veuillez réessayer";
    private static final String ACCOUNT_DISABLED = "Votre compte a été désactivé. Veuillez contacter votre administrateur";
    private static final String ERROR_PROCESSING_FILE = "Erreur survenue lors du traitement du fichier";
    private static final String FILE_UPLOADED_MAX_SIZE_MSG = "Taille de fichier trop grande";
    private static final String NOT_ENOUGH_PERMISSION = "Vous n'avez assez de droits pour exécuter cette action";
    private static final String NO_RESULT = "Aucun résultat";
    private static final String MISSING_FORM_FIELD = "Au moins un champ n'est pas renseigné dans le formulaire";

    @ExceptionHandler(DisabledException.class) // Compte désactivé
    public ResponseEntity<HttpCustomResponse> accountDisabledException(DisabledException ex) {
        log.error(ex.getMessage());
        return createHttpResponse(BAD_REQUEST, ACCOUNT_DISABLED, ex);
    }

    @ExceptionHandler(BadCredentialsException.class) // identifiants incorrects
    public ResponseEntity<HttpCustomResponse> badCredentialsException(BadCredentialsException ex) {
        log.error(ex.getMessage());
        return createHttpResponse(BAD_REQUEST, INCORRECT_CREDENTIALS, ex);
    }

    @ExceptionHandler(AccessDeniedException.class) // accès refusé
    public ResponseEntity<HttpCustomResponse> accessDeniedException(AccessDeniedException ex) {
        log.error(ex.getMessage());
        return createHttpResponse(FORBIDDEN, NOT_ENOUGH_PERMISSION, ex);
    }

    @ExceptionHandler(LockedException.class) // Compte bloqué
    public ResponseEntity<HttpCustomResponse> LockedException(LockedException ex) {
        log.error(ex.getMessage());
        return createHttpResponse(UNAUTHORIZED, ACCOUNT_LOCKED, ex);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<HttpCustomResponse> LockedException(MissingServletRequestParameterException ex) {
        log.error(ex.getMessage());
        return createHttpResponse(UNAUTHORIZED, MISSING_FORM_FIELD, ex);
    }

    @ExceptionHandler(TokenExpiredException.class) // token expiré - Auth0
    public ResponseEntity<HttpCustomResponse> tokenExpiredException(TokenExpiredException ex) {
        log.error(ex.getMessage());
        return createHttpResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), ex);
    }

    @ExceptionHandler(EmailExistsException.class) // Email déjà utilisé
    public ResponseEntity<HttpCustomResponse> emailExistsException(EmailExistsException ex) {
        log.error(ex.getMessage());
        return createHttpResponse(BAD_REQUEST, ex.getMessage(), ex);
    }

    @ExceptionHandler(UsernameExistsException.class) // Email déjà utilisé
    public ResponseEntity<HttpCustomResponse> usernameExistsException(UsernameExistsException ex) {
        log.error(ex.getMessage());
        return createHttpResponse(BAD_REQUEST, ex.getMessage(), ex);
    }

    @ExceptionHandler(EmailNotFoundException.class) // Email non trouvée
    public ResponseEntity<HttpCustomResponse> emailNotFoundException(EmailNotFoundException ex) {
        log.error(ex.getMessage());
        return createHttpResponse(BAD_REQUEST, ex.getMessage(), ex);
    }

    @ExceptionHandler(UserNotFoundException.class) // User non trouvée
    public ResponseEntity<HttpCustomResponse> userNotFoundException(UserNotFoundException ex) {
        log.error(ex.getMessage());
        return createHttpResponse(BAD_REQUEST, ex.getMessage(), ex);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class) // method Http non supportée
    public ResponseEntity<HttpCustomResponse> methodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        HttpMethod httpMethod = Objects.requireNonNull(ex.getSupportedHttpMethods()).iterator().next();
        log.error(ex.getMessage());
        return createHttpResponse(BAD_REQUEST, String.format(METHOD_IS_NOT_ALLOWED, httpMethod), ex);
    }

    @ExceptionHandler(Exception.class) // Erreur interne
    public ResponseEntity<HttpCustomResponse> internalServerErrorException(Exception ex) {
        log.error(ex.getMessage());
        //ex.printStackTrace();
        return createHttpResponse(INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR_MSG, ex);
    }

    @ExceptionHandler(NoResultException.class) // Ressource non trouvée
    public ResponseEntity<HttpCustomResponse> notFoundException(NoResultException ex) {
        log.error(ex.getMessage());
        return createHttpResponse(NOT_FOUND, NO_RESULT, ex);
    }

    @ExceptionHandler(EmptyResultDataAccessException.class) // Ressource non trouvée
    public ResponseEntity<HttpCustomResponse> notFoundException(EmptyResultDataAccessException ex) {
        log.error(ex.getMessage());
        return createHttpResponse(NOT_FOUND, NO_RESULT, ex);
    }

    @ExceptionHandler(IOException.class) // Erreurs concernant fichiers
    public ResponseEntity<HttpCustomResponse> iOException(IOException ex) {
        log.error(ex.getMessage());
        return createHttpResponse(INTERNAL_SERVER_ERROR, ERROR_PROCESSING_FILE, ex);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class) // Erreurs concernant fichiers
    public ResponseEntity<HttpCustomResponse> iOException(MaxUploadSizeExceededException ex) {
        log.error(ex.getMessage());
        return createHttpResponse(INTERNAL_SERVER_ERROR, FILE_UPLOADED_MAX_SIZE_MSG, ex);
    }

    @RequestMapping(ERROR_PATH) // page non trouvée
    public ResponseEntity<HttpCustomResponse> notFound404() {
        return createHttpResponse(NOT_FOUND, NO_RESOURCE_FOUND, null);
    }

    private ResponseEntity<HttpCustomResponse> createHttpResponse(
            HttpStatus httpStatus,
            String message,
            Exception exception) {
        HttpCustomResponse httpCustomResponse = HttpCustomResponse.builder()
                .httpStatus(httpStatus)
                .httpStatusCode(httpStatus.value())
                .reason(httpStatus.getReasonPhrase().toUpperCase())
                .message(message.toUpperCase())
                .build();
        if (exception != null) {
            String devMessage = "ExceptionClass: " + exception.getClass().getSimpleName() +
                    " || Message: " + exception.getMessage().toUpperCase() +
                    " || ExceptionClassName: " + exception.getClass().getName();
            httpCustomResponse.setDeveloperMessage(devMessage.trim());
        }
        // Constructeur: @Nullable T body, HttpStatus status
        return new ResponseEntity<>(httpCustomResponse, httpStatus);
    }


}
