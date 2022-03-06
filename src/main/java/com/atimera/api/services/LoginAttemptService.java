package com.atimera.api.services;

/**
 * Ce service permet de limiter les Brute Force Attack
 * Ex: En limitant le nombre de tentatives de connexion sur une courte période
 */
public interface LoginAttemptService {

    void evictUserToLoginAttemptCache(String username);

    void addUserToLoginAttemptCache(String username);

    boolean hasExceededMaxAttempts(String username);
}
