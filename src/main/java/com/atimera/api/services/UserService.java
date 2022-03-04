package com.atimera.api.services;

import com.atimera.api.domain.User;
import com.atimera.api.exception.domain.EmailExistsException;
import com.atimera.api.exception.domain.EmailNotFoundException;
import com.atimera.api.exception.domain.UsernameExistsException;

import java.util.List;

public interface UserService {

    User register(String firstName, String lastName, String username, String email)
            throws EmailNotFoundException, UsernameExistsException, EmailExistsException;

    List<User> getUsers();

    User findByUsername(String username);

    User findUserByEmail(String email);
}
