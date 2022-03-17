package com.atimera.api.services.impl;

import com.atimera.api.configuration.PasswordEncoder;
import com.atimera.api.constant.FileConstant;
import com.atimera.api.domain.User;
import com.atimera.api.domain.UserPrincipal;
import com.atimera.api.enumeration.Role;
import com.atimera.api.exception.domain.EmailExistsException;
import com.atimera.api.exception.domain.EmailNotFoundException;
import com.atimera.api.exception.domain.UsernameExistsException;
import com.atimera.api.repositories.UserRepository;
import com.atimera.api.services.LoginAttemptService;
import com.atimera.api.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.List;

import static com.atimera.api.constant.FileConstant.*;
import static com.atimera.api.constant.UserConstant.*;
import static com.atimera.api.enumeration.Role.ROLE_USER;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.apache.commons.lang3.StringUtils.EMPTY;

@Service
@Transactional
@Qualifier("UserDetailsService")
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final LoginAttemptService loginAttemptService;
    //private final EmailService emailService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            log.error(String.format(NO_USER_FOUND_BY_USERNAME, username));
            throw new UsernameNotFoundException(String.format(NO_USER_FOUND_BY_USERNAME, username));
        } else {
            // Ici L'utilisateur est enregistré en base
            validateLoginAttemptForUser(user);
            user.setLastLoginDateDisplay(user.getLastLoginDate());
            user.setLastLoginDate(new Date());
            userRepository.save(user);

            UserPrincipal userPrincipal = new UserPrincipal(user);
            log.info(String.format(NO_USER_FOUND_BY_USERNAME, username));
            return userPrincipal;
        }
    }

    @Override
    public User register(String firstName, String lastName, String username, String email, String password)
            throws EmailNotFoundException, UsernameExistsException, EmailExistsException {
        validateNewUsernameAndEmail(EMPTY, username, email);
        User user = new User();
        user.setUserId(generateUserId());
        String vPassword = password == null ? generatePassword() : password;
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setEmail(email);
        user.setJoinDate(new Date());
        user.setPassword(encodePassword(vPassword));
        user.setActive(true);
        user.setNotLocked(true);
        user.setRole(ROLE_USER.name());
        user.setAuthorities(ROLE_USER.getAuthorities());
        user.setProfileImageUrl(getTemporaryProfileImageUrl(username));
        userRepository.save(user);
        log.info("Mot de passe du nouvel utilisateur : " + password);
        //emailService.sendNewPasswordEmail(user.getFirstName(), password, user.getEmail());
        return user;
    }

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public List<User> list(int limit) {
        return userRepository.findAll(PageRequest.of(0, limit)).toList();
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User addUser(String firstName, String lastName, String username, String email,
                        String role, boolean isNotLocked, boolean isActive, MultipartFile profileImage)
            throws EmailNotFoundException, UsernameExistsException, EmailExistsException, IOException {
        validateNewUsernameAndEmail(EMPTY, username, email);
        User user = new User();
        String password = generatePassword();
        user.setUserId(generateUserId());
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setEmail(email);
        user.setJoinDate(new Date());
        user.setPassword(encodePassword(password));
        user.setActive(isActive);
        user.setNotLocked(isNotLocked);
        user.setRole(getRoleEnumName(role).name());
        user.setAuthorities(getRoleEnumName(role).getAuthorities());
        user.setProfileImageUrl(getTemporaryProfileImageUrl(username));
        userRepository.save(user);
        saveProfileImage(user, profileImage);
        return user;
    }

    @Override
    public User updateUser(String currentUsername, String newFirstName, String newLastName, String newUsername,
                           String newEmail, String role, boolean isNotLocked, boolean isActive, MultipartFile newProfileImage)
            throws EmailNotFoundException, UsernameExistsException, EmailExistsException, IOException {
        User currentUser = validateNewUsernameAndEmail(currentUsername, newUsername, newEmail);
        if (currentUser == null) {
            throw new EmailNotFoundException(String.format(NO_USER_FOUND_BY_USERNAME, currentUsername));
        }
        currentUser.setFirstName(newFirstName);
        currentUser.setLastName(newLastName);
        currentUser.setUsername(newUsername);
        currentUser.setEmail(newEmail);
        currentUser.setActive(isActive);
        currentUser.setNotLocked(isNotLocked);
        currentUser.setRole(getRoleEnumName(role).name());
        currentUser.setAuthorities(getRoleEnumName(role).getAuthorities());
        userRepository.save(currentUser);
        saveProfileImage(currentUser, newProfileImage);
        return currentUser;
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public void resetPassword(String email) throws EmailNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new EmailNotFoundException(String.format(NO_USER_FOUND_BY_EMAIL, email));
        }
        String password = generatePassword();
        user.setPassword(encodePassword(password));
        userRepository.save(user);
        log.info("Le nouveau mot de passe de {} est: {}", user.getFirstName(), password);
        //emailService.sendNewPasswordEmail(user.getFirstName(), user.getPassword(), user.getEmail());
    }

    @Override
    public User updateProfileImage(String username, MultipartFile profileImage)
            throws EmailNotFoundException, UsernameExistsException, EmailExistsException, IOException {
        User user = validateNewUsernameAndEmail(username, null, null);
        saveProfileImage(user, profileImage);
        return user;
    }

    private void saveProfileImage(User user, MultipartFile profileImage) throws IOException {
        if (profileImage != null) {
            Path userFolder = Path.of(USER_FOLDER + user.getUsername()).toAbsolutePath().normalize();
            if (!Files.exists(userFolder)) {
                Files.createDirectories(userFolder);
                log.info("Dossier utilisateur créé pour: {}", user.getUsername());
            }
            Files.deleteIfExists(Path.of(userFolder + user.getUsername() + DOT + JPEG_EXTENSION));
            log.info("Suppression ancienne image: {}", userFolder + user.getUsername() + DOT + JPEG_EXTENSION);

            Files.copy(profileImage.getInputStream(),
                    userFolder.resolve(user.getUsername() + DOT + JPEG_EXTENSION),
                    REPLACE_EXISTING);
            // On ajoute REPLACE_EXISTING pour être certaine que la nouvelle remplacera ancienne
            // même si on aurait pu se limiter à la suppression: ligne au dessus.
            log.info("Sauvegarde nouvelle image: {}", userFolder.resolve(user.getUsername() + DOT + JPEG_EXTENSION));
            user.setProfileImageUrl(getProfileImageUrl(user.getUsername()));

            userRepository.save(user);
            log.info(SAVED_FILE_IN_FILE_SYSTEM + profileImage.getOriginalFilename());
        }
    }

    private String getTemporaryProfileImageUrl(String username) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(FileConstant.DEFAULT_USER_PROFILE_IMAGE_PATH + username)
                .toUriString();
    }

    private String getProfileImageUrl(String username) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(USER_IMAGE_PATH + username + FORWARD_SLASH + username + DOT + JPEG_EXTENSION)
                .toUriString();
    }

    private Role getRoleEnumName(String role) {
        return Role.valueOf(role.toUpperCase());
    }


    private void validateLoginAttemptForUser(User user) {
        if (user.isNotLocked()) {
            user.setNotLocked(
                    !loginAttemptService.hasExceededMaxAttempts(user.getUsername()));
        } else {
            loginAttemptService.evictUserToLoginAttemptCache(user.getUsername());
        }
    }

    private User validateNewUsernameAndEmail(String currentUsername, String newUsername, String newEmail)
            throws EmailExistsException, UsernameExistsException, EmailNotFoundException {
        User userByNewUsername = findByUsername(newUsername);
        User userByNewEmail = findUserByEmail(newEmail);
        if (StringUtils.isNotBlank(currentUsername)) {
            User currentUser = findByUsername(currentUsername);
            if (currentUser == null) {
                throw new EmailNotFoundException(String.format(NO_USER_FOUND_BY_USERNAME, currentUsername));
            }
            if (userByNewUsername != null && !currentUser.getId().equals(userByNewUsername.getId())) {
                throw new UsernameExistsException(USERNAME_ALREADY_EXISTS);
            }
            if (userByNewEmail != null && !currentUser.getId().equals(userByNewEmail.getId())) {
                throw new EmailExistsException(EMAIL_ALREADY_EXISTS);
            }
            return currentUser;
        } else {
            if (userByNewUsername != null) {
                throw new UsernameExistsException(USERNAME_ALREADY_EXISTS);
            }
            if (userByNewEmail != null) {
                throw new EmailExistsException(EMAIL_ALREADY_EXISTS);
            }
            return null;
        }
    }

    private String encodePassword(String password) {
        return passwordEncoder.bCryptPasswordEncoder().encode(password);
    }

    private String generatePassword() {
        return RandomStringUtils.randomAlphanumeric(10);
    }

    private String generateUserId() {
        return RandomStringUtils.randomNumeric(10);
    }


}
