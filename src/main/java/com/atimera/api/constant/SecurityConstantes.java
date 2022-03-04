package com.atimera.api.constant;

public class SecurityConstantes {
    public static final long EXPIRATION_TIME = 423_000_000; // 5 jours exprimés en milli-secondes
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String JWT_TOKEN_HEADER = "Jwt-Token";
    public static final String TOKEN_CANNOT_BE_VERIFIED = "Token cannot be verified";
    public static final String ATIMERA_LLC = "Amdiatou TIMERA, LLC";
    public static final String ATIAPI_ADMINISTRATION = "Portail de gestion d'utilisateurs";
    public static final String AUTHORITIES = "Portail de gestion d'utilisateurs";
    public static final String FORBIDDEN_MESSAGE = "Vous devez vous connecter pour accéder à cette page.";
    public static final String ACCESS_DENIED_MESSAGE = "Vous n'avez pas la permission d'accéder à cette page.";
    public static final String OPTIONS_HTTP_METHOD = "OPTIONS";
    //public static final String[] PUBLIC_URLS = {"/user/login", "user/register", "/user/resetpassword/**", "user/image/**"};
    public static final String[] PUBLIC_URLS = {"**"};
}
