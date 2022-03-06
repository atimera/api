package com.atimera.api.services;

import com.atimera.api.domain.User;
import com.atimera.api.exception.domain.EmailExistsException;
import com.atimera.api.exception.domain.EmailNotFoundException;
import com.atimera.api.exception.domain.UsernameExistsException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UserService {

    User register(String firstName, String lastName, String username, String email)
            throws EmailNotFoundException, UsernameExistsException, EmailExistsException;

    List<User> getUsers();

    List<User> list(int limit);

    User findByUsername(String username);

    User findUserByEmail(String email);

    User addUser(String firstName, String lastName, String username, String email, String role,
                 boolean isNotLocked, boolean isActive, MultipartFile profileImage) throws EmailNotFoundException, UsernameExistsException, EmailExistsException, IOException;

    User updateUser(String currentUsername, String newFirstName, String newLastName, String newUsername, String newEmail,
                    String role, boolean isNotLocked, boolean isActive, MultipartFile newProfileImage) throws EmailNotFoundException, UsernameExistsException, EmailExistsException, IOException;

    void deleteUser(Long id);

    void resetPassword(String email) throws EmailNotFoundException;

    User updateProfileImage(String username, MultipartFile profileImage) throws EmailNotFoundException, UsernameExistsException, EmailExistsException, IOException;
}
