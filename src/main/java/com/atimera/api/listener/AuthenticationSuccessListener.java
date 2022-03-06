package com.atimera.api.listener;

import com.atimera.api.domain.UserPrincipal;
import com.atimera.api.services.LoginAttemptService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthenticationSuccessListener {
    private final LoginAttemptService loginAttemptService;


    // Enlever l'entrée concernant l'utilisateur s'il a réussi à se connecter
    @EventListener
    public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
        Object principal = event.getAuthentication().getPrincipal();
        // Retourne un UserPrincipal car loadUserByUsername retourne un UserPrincipal
        if (principal instanceof UserPrincipal) {
            UserPrincipal userPrincipal = (UserPrincipal) event.getAuthentication().getPrincipal();
            loginAttemptService.evictUserToLoginAttemptCache(userPrincipal.getUsername());
        }
    }
}
