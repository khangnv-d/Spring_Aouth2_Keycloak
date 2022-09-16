package com.example.demo.config;

import com.example.demo.constants.KeycloakClientInfo;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakClientConfig {

    @Value(KeycloakClientInfo.KEYCLOAK_CLIENT_SECRET)
    private String ClientSecret;
    @Value(KeycloakClientInfo.KEYCLOAK_CLIENT_ID)
    private String clientId;
    @Value(KeycloakClientInfo.KEYCLOAK_REDIRECT_URI)
    private String authUrl;
    @Value(KeycloakClientInfo.KEYCLOAK_REALM)
    private String realm;

    //    @Bean
    public Keycloak getKeycloak() {
        return KeycloakBuilder.builder()
                .serverUrl(authUrl)
                .realm(realm)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .clientId(clientId)
                .clientSecret(ClientSecret)
                .build();
    }

}
