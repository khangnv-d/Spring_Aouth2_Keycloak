package com.example.demo.user;

import com.example.demo.config.KeycloakClientConfig;
import com.example.demo.constants.KeycloakClientInfo;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.Response;
import java.util.List;

@Service
@Slf4j
public class UserService {
    //    private Keycloak keycloak;
    private final KeycloakClientConfig keycloakClientConfig;
    private final Keycloak keycloak;

    public UserService(KeycloakClientConfig keycloakClientConfig) {
        this.keycloakClientConfig = keycloakClientConfig;
        this.keycloak = keycloakClientConfig.getKeycloak();
    }

    @Value(KeycloakClientInfo.KEYCLOAK_REALM)
    private String realm;

    public List<UserRepresentation> findAllUsers() {
        return keycloak.realm(realm).users().list();
    }

    public List<UserRepresentation> findByUsername(String username) {
        return keycloak.realm(realm).users().search(username);
    }

    public UserRepresentation findById(String id) {
        return keycloak.realm(realm).users().get(id).toRepresentation();
    }

    public void assignRole(String userId, RoleRepresentation role) {
        keycloak.realm(realm).users().get(userId).roles().realmLevel().add((List<RoleRepresentation>) role);
    }

    public Response createUser(User userRequest) {
        CredentialRepresentation password = preparePasswordRepresentation(userRequest.getPassword());
        UserRepresentation user = prepareUserRepresentation(userRequest, password);

        return keycloak.realm(realm).users().create(user);
    }

    private CredentialRepresentation preparePasswordRepresentation(String password) {
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setTemporary(false);
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);

        return credential;
    }

    private UserRepresentation prepareUserRepresentation(User user, CredentialRepresentation credential) {
        UserRepresentation newUser = new UserRepresentation();
        newUser.setUsername(user.getUsername());
        newUser.setCredentials((List<CredentialRepresentation>) credential);
        newUser.setEnabled(true);

        return newUser;
    }

}
