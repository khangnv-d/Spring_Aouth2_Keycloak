package com.example.demo.constants;

import org.springframework.context.annotation.Configuration;

import java.util.Set;

@Configuration
public class KeycloakClientInfo {

    public static final String KEYCLOAK_REALM = "khangnv";
    public static final String KEYCLOAK_CLIENT_ID = "oauth2-demo-client";
    public static final String KEYCLOAK_CLIENT_SECRET = "iQnFZyuvKMvpwsI0rcbEB1nEF2rbRQBN";
    public static final String KEYCLOAK_CLIENT_NAME = "KhangNV";
    public static final String KEYCLOAK_AUTHORIZATION_GRANT_TYPE = "authorization_code";
    public static final String KEYCLOAK_REDIRECT_URI = "http://localhost:8080";
    public static final Set<String> KEYCLOAK_SCOPE = Set.of("openid", "profile", "email");

}
