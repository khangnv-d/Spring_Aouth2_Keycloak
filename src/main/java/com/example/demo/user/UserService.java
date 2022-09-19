package com.example.demo.user;

import com.example.demo.apiexception.RequestException;
import com.example.demo.config.KeycloakClientConfig;
import com.example.demo.constants.KeycloakClientInfo;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
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
        if (!isUserExisted(id)) {
            throw new RequestException("Not Found User Has Id " + id);
        } else return keycloak.realm(realm).users().get(id).toRepresentation();
    }

    public void assignRole(String userId, RoleRepresentation role) {
        keycloak.realm(realm).users().get(userId).roles().realmLevel().add((List<RoleRepresentation>) role);
    }

    public Response createUser(User userRequest) {
        CredentialRepresentation password = preparePasswordRepresentation(userRequest.getPassword());
        List<CredentialRepresentation> credentialList = new ArrayList<>();
        credentialList.add(password);
        UserRepresentation user = prepareUserRepresentation(userRequest, credentialList);

        return keycloak.realm(realm).users().create(user);
    }

    private CredentialRepresentation preparePasswordRepresentation(String password) {
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setTemporary(false);
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);

        return credential;
    }

    private UserRepresentation prepareUserRepresentation(User user, List<CredentialRepresentation> credential) {
        UserRepresentation newUser = new UserRepresentation();

        String username = user.getEmail();
        if (isUsernameTaken(username)) throw new RequestException("Username" + username + " has been taken ");
        else newUser.setUsername(user.getUsername());

        String email = user.getEmail();
        if (isEmailTaken(email)) throw new RequestException("Email" + email + " has been taken ");
        else newUser.setEmail(user.getEmail());

        newUser.setCredentials(credential);
        newUser.setEnabled(true);

        return newUser;
    }

    //    @Transactional
    public void updateUser(String id, User updateUser) {

        if (!isUserExisted(id)) {
            throw new RequestException("Not found user has Id " + id);
        } else {
            UserRepresentation user = findById(id);
            UserResource userResource = getUserResource(id);
            user.setUsername(updateUser.getUsername());
            CredentialRepresentation password = preparePasswordRepresentation(updateUser.getPassword());
            userResource.resetPassword(password);
            user.setEmail(updateUser.getEmail());

            userResource.update(user);
        }
    }

    public void deleteUser(String id) {

        if (!isUserExisted(id)) {
            throw new RequestException("Not found user has Id " + id);
        } else {
            UserRepresentation user = findById(id);
            user.setEnabled(false);
            getUserResource(id).update(user);
        }
    }

    private UserResource getUserResource(String id) {
        return keycloak.realm(realm).users().get(id);
    }

    private boolean isUserExisted(String id) {
        return findAllUsers().stream().anyMatch(user -> user.getId().equals(id));
    }

    private boolean isUsernameTaken(String username) {
        return findAllUsers().stream().anyMatch(u -> u.getUsername().equals(username));
    }

    private boolean isEmailTaken(String email) {
        return findAllUsers().stream().anyMatch(u -> u.getEmail().equals(email));
    }

    private List<CredentialRepresentation> addAttributeToListCredential(CredentialRepresentation field) {
        List<CredentialRepresentation> credentialList = new ArrayList<>();
        credentialList.add(field);

        return credentialList;
    }

}
