package com.example.demo.role;

import com.example.demo.constants.KeycloakClientInfo;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {

    private Keycloak keycloak;
    @Value(KeycloakClientInfo.KEYCLOAK_REALM)
    private String realm;

    public List<RoleRepresentation> findAllRole() {
        return keycloak.realm(realm).roles().list();
    }

    public RoleRepresentation findRoleByName(String roleName) {
        return keycloak.realm(realm).roles().get(roleName).toRepresentation();
    }

    public void createRole(String roleName) {
        RoleRepresentation role = new RoleRepresentation();
        role.setName(roleName);

        keycloak.realm(realm).roles().create(role);
    }

}
