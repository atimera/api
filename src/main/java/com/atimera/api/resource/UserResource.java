package com.atimera.api.resource;

import com.atimera.api.configuration.JwtTokenProvider;
import com.atimera.api.domain.User;
import com.atimera.api.domain.UserPrincipal;
import com.atimera.api.dto.HttpCustomResponse;
import com.atimera.api.exception.ExceptionHandling;
import com.atimera.api.exception.domain.EmailExistsException;
import com.atimera.api.exception.domain.EmailNotFoundException;
import com.atimera.api.exception.domain.UsernameExistsException;
import com.atimera.api.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Map;

import static com.atimera.api.constant.SecurityConstantes.JWT_TOKEN_HEADER;

@RestController
@RequestMapping(path = {"/", "/user"})
@RequiredArgsConstructor
public class UserResource extends ExceptionHandling {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    public ResponseEntity<HttpCustomResponse> login(@RequestBody @Valid User user) {
        authenticate(user.getUsername(), user.getPassword());
        User loginUser = userService.findByUsername(user.getUsername());
        UserPrincipal userPrincipal = new UserPrincipal(loginUser);
        HttpHeaders jwtHeader = getJwtHeader(userPrincipal);
        return new ResponseEntity<>(
                HttpCustomResponse.builder()
                        .httpStatusCode(HttpStatus.OK.value())
                        .httpStatus(HttpStatus.OK).message("Utilisateur connecté")
                        .data(Map.of("user", loginUser, "jwtHeader", jwtHeader))
                        .build(), HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<HttpCustomResponse> register(@RequestBody @Valid User user)
            throws EmailNotFoundException, UsernameExistsException, EmailExistsException {
        User newUser = userService.register(user.getFirstName(), user.getLastName(), user.getUsername(), user.getEmail());
        return new ResponseEntity<>(
                HttpCustomResponse.builder()
                        .httpStatusCode(HttpStatus.CREATED.value())
                        .httpStatus(HttpStatus.CREATED).message("Utilisateur inscrit")
                        .data(Map.of("user", newUser)).build(), HttpStatus.OK);
    }

    private HttpHeaders getJwtHeader(UserPrincipal userPrincipal) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JWT_TOKEN_HEADER, jwtTokenProvider.generateJwtToken(userPrincipal));
        return httpHeaders;
    }

    // Essaie d'authentifier l'utilisateur ou lève une exception
    private void authenticate(String username, String password) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
    }

}
