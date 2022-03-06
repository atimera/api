package com.atimera.api.resource;

import com.atimera.api.configuration.JwtTokenProvider;
import com.atimera.api.domain.User;
import com.atimera.api.domain.UserPrincipal;
import com.atimera.api.dto.HttpCustomResponse;
import com.atimera.api.exception.ExceptionHandling;
import com.atimera.api.exception.domain.EmailExistsException;
import com.atimera.api.exception.domain.EmailNotFoundException;
import com.atimera.api.exception.domain.UserNotFoundException;
import com.atimera.api.exception.domain.UsernameExistsException;
import com.atimera.api.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static com.atimera.api.constant.FileConstant.*;
import static com.atimera.api.constant.SecurityConstant.JWT_TOKEN_HEADER;
import static com.atimera.api.constant.UserConstant.*;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;

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
                        .httpStatusCode(OK.value())
                        .httpStatus(OK).message(USER_LOGIN_SUCCESS_MSG)
                        .data(Map.of("user", loginUser, "jwtHeader", jwtHeader))
                        .build(), OK);
    }

    @PostMapping("/register")
    public ResponseEntity<HttpCustomResponse> register(@RequestBody @Valid User user)
            throws EmailNotFoundException, UsernameExistsException, EmailExistsException, UserNotFoundException {
        User newUser = userService.register(user.getFirstName(), user.getLastName(), user.getUsername(), user.getEmail());
        return new ResponseEntity<>(
                HttpCustomResponse.builder()
                        .httpStatusCode(CREATED.value())
                        .httpStatus(CREATED).message(USER_REGISTER_SUCCESS_MSG)
                        .data(Map.of("user", newUser))
                        .build(), OK);
    }

    @PostMapping("/add")
    //@PreAuthorize("hasAnyAuthority('user:create')")
    public ResponseEntity<HttpCustomResponse> add(
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("username") String username,
            @RequestParam("email") String email,
            @RequestParam("role") String role,
            @RequestParam("isActive") String isActive,
            @RequestParam("isNotLocked") String isNotLocked,
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage
    ) throws EmailNotFoundException, UsernameExistsException, EmailExistsException, IOException {
        User newUser = userService.addUser(
                firstName, lastName, username, email, role, Boolean.parseBoolean(isNotLocked),
                Boolean.parseBoolean(isActive), profileImage);
        return new ResponseEntity<>(
                HttpCustomResponse.builder()
                        .httpStatusCode(CREATED.value())
                        .httpStatus(CREATED).message(USER_ADDED_MSG)
                        .data(Map.of("user", newUser))
                        .build(), OK);
    }

    @PostMapping("/update")
    @PreAuthorize("hasAnyAuthority('user:update')")
    public ResponseEntity<HttpCustomResponse> update(
            @RequestParam("currentUsername") String currentUsername,
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("username") String username,
            @RequestParam("email") String email,
            @RequestParam("role") String role,
            @RequestParam("isActive") String isActive,
            @RequestParam("isNotLocked") String isNotLocked,
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage
    ) throws EmailNotFoundException, UsernameExistsException, EmailExistsException, IOException {
        User updatedUser = userService.updateUser(
                currentUsername, firstName, lastName, username, email, role,
                Boolean.parseBoolean(isNotLocked), Boolean.parseBoolean(isActive), profileImage);
        return new ResponseEntity<>(
                HttpCustomResponse.builder()
                        .httpStatusCode(CREATED.value())
                        .httpStatus(CREATED).message(USER_UPDATED_MSG)
                        .data(Map.of("user", updatedUser))
                        .build(), OK);
    }

    @GetMapping("/find/{username}")
    public ResponseEntity<HttpCustomResponse> getUser(@PathVariable("username") String username) {
        User user = userService.findByUsername(username);
        HttpCustomResponse httpCustomResponse = HttpCustomResponse.builder()
                .httpStatusCode(OK.value())
                .httpStatus(OK).message(USER_FOUND_BY_USERNAME_MSG)
                .build();
        if (user != null) {
            httpCustomResponse.setData(Map.of("user", user));
        }
        return new ResponseEntity<>(httpCustomResponse, OK);
    }

    @GetMapping("/list")
    public ResponseEntity<HttpCustomResponse> getAllUsers() {
        List<User> users = userService.getUsers();
        return new ResponseEntity<>(
                HttpCustomResponse.builder()
                        .httpStatusCode(OK.value())
                        .httpStatus(OK).message(USER_LIST_MSG)
                        .data(Map.of("users", users))
                        .build(), OK);
    }

    @GetMapping("/reset-password/{email}")
    public ResponseEntity<HttpCustomResponse> resetPassword(@PathVariable("email") String email)
            throws EmailNotFoundException {
        userService.resetPassword(email);
        return new ResponseEntity<>(
                HttpCustomResponse.builder()
                        .httpStatusCode(OK.value())
                        .httpStatus(OK).message(String.format(USER_RESET_PASSWORD_MSG, email))
                        .build(), OK);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('user:delete')")
    public ResponseEntity<HttpCustomResponse> delete(@PathVariable("id") Long id) {
        userService.deleteUser(id);
        return new ResponseEntity<>(
                HttpCustomResponse.builder()
                        .httpStatusCode(NO_CONTENT.value())
                        .httpStatus(NO_CONTENT).message(USER_DELETE_MSG)
                        .build(), OK);
    }

    @PostMapping("/update-profile-image")
    public ResponseEntity<HttpCustomResponse> updateProfileImage(
            @RequestParam("username") String username,
            @RequestParam("profileImage") MultipartFile profileImage
    ) throws EmailNotFoundException, UsernameExistsException, EmailExistsException, IOException {
        User updatedUser = userService.updateProfileImage(username, profileImage);
        return new ResponseEntity<>(
                HttpCustomResponse.builder()
                        .httpStatusCode(CREATED.value())
                        .httpStatus(CREATED).message(USER_UPDATE_IMAGE_PROFILE_MSG)
                        .data(Map.of("user", updatedUser))
                        .build(), OK);
    }

    @GetMapping(path = "/image/{username}/{filename}", produces = IMAGE_JPEG_VALUE)
    public byte[] getImageProfile(
            @PathVariable("username") String username, @PathVariable("filename") String filename) throws IOException {
        return Files.readAllBytes(
                Path.of(USER_FOLDER + username + FORWARD_SLASH + filename)
        );
    }

    @GetMapping(path = "/image/temp-profile/{username}", produces = IMAGE_JPEG_VALUE)
    public byte[] getTempImageProfile(@PathVariable("username") String username) throws IOException {
        URL url = new URL(TEMP_PROFILE_IMAGE_BASE_URL + username);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // Lit l'image récupérée depuis l'URL par défaut + username
        try (InputStream inputStream = url.openStream()) {
            int byteRead;
            byte[] chunk = new byte[1024];
            while ((byteRead = inputStream.read(chunk)) > 0) {
                byteArrayOutputStream.write(chunk, 0, byteRead);
            }
        }
        return byteArrayOutputStream.toByteArray();
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
