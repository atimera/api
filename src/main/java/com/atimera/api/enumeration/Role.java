package com.atimera.api.enumeration;

import static com.atimera.api.constant.Authority.*;

public enum Role {
    // Pour chaque Role on dit quelles sont ses permissions
    ROLE_USER(USER_AUTHORITIES),
    ROLE_MANAGER(MANAGER_AUTHORITIES),
    ROLE_ADMIN(ADMIN_AUTHORITIES),
    ROLE_SUPER_USER(SUPER_ADMIN_AUTHORITIES);

    private final String[] authorities; // Les permissions

    Role(String... authorities) {
        this.authorities = authorities;
    }

    public String[] getAuthorities() {
        return authorities;
    }
}
