package com.atimera.api.services.impl;

import com.atimera.api.configuration.PasswordEncoder;
import com.atimera.api.domain.User;
import com.atimera.api.domain.UserPrincipal;
import com.atimera.api.exception.domain.EmailExistsException;
import com.atimera.api.exception.domain.EmailNotFoundException;
import com.atimera.api.exception.domain.UsernameExistsException;
import com.atimera.api.repositories.UserRepository;
import com.atimera.api.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Date;
import java.util.List;

import static com.atimera.api.constant.UserServiceConstant.*;
import static com.atimera.api.enumeration.Role.ROLE_USER;

@Service
@Transactional
@Qualifier("UserDetailsService")
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            log.error(String.format(NO_USER_FOUND_BY_USERNAME, username));
            throw new UsernameNotFoundException(String.format(NO_USER_FOUND_BY_USERNAME, username));
        } else {
            user.setLastLoginDateDisplay(user.getLastLoginDate());
            user.setLastLoginDate(new Date());
            userRepository.save(user);

            UserPrincipal userPrincipal = new UserPrincipal(user);
            log.info(String.format(NO_USER_FOUND_BY_USERNAME, username));
            return userPrincipal;
        }
    }

    @Override
    public User register(String firstName, String lastName, String username, String email)
            throws EmailNotFoundException, UsernameExistsException, EmailExistsException {
        validateNewUsernameAndEmail(StringUtils.EMPTY, username, email);
        User user = new User();
        user.setUserId(generateUserId());
        String password = generatePassword();
        String encodedPassword = encodePassword(password);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setEmail(email);
        user.setJoinDate(new Date());
        user.setPassword(encodedPassword);
        user.setActive(true);
        user.setNotLocked(true);
        user.setRole(ROLE_USER.name());
        user.setAuthorities(ROLE_USER.getAuthorities());
        user.setProfileImageUrl(getTemporaryImageUrl());
        userRepository.save(user);
        log.info("Mot de passe du nouvel utilisateur : " + password);
        return user;
    }

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
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

    private String getTemporaryImageUrl() {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(DEFAULT_USER_PROFILE_IMAGE_PATH)
                .toUriString();
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
