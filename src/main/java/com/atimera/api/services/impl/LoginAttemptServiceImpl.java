package com.atimera.api.services.impl;


import com.atimera.api.services.LoginAttemptService;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;


@Service
@Slf4j
public class LoginAttemptServiceImpl implements LoginAttemptService {
    private static final int MAXIMUM_NUMBER_OF_ATTEMPTS = 5;
    private static final int ATTEMPT_INCREMENT = 1;
    private final LoadingCache<String, Integer> loginAttemptCache;

    LoginAttemptServiceImpl() {
        super();
        loginAttemptCache = CacheBuilder.newBuilder()
                .expireAfterWrite(15, TimeUnit.MINUTES)
                .maximumSize(100) // nombre max d'entrées dans le cache
                .build(new CacheLoader<String, Integer>() {
                    public Integer load(String key) {
                        return 0;
                    }
                });
    }

    @Override
    public void evictUserToLoginAttemptCache(String username) {
        loginAttemptCache.invalidate(username);
    }

    @Override
    public void addUserToLoginAttemptCache(String username) {
        try {
            int attempts = 0;
            attempts = ATTEMPT_INCREMENT + loginAttemptCache.get(username);
            loginAttemptCache.put(username, attempts);
        } catch (ExecutionException ex) {
            log.error(ex.getMessage());
        }
    }

    @Override
    public boolean hasExceededMaxAttempts(String username) {
        try {
            return loginAttemptCache.get(username) >= MAXIMUM_NUMBER_OF_ATTEMPTS;
        } catch (ExecutionException ex) {
            log.error(ex.getMessage());
        }
        return false;
    }
}
