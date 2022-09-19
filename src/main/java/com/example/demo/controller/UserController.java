package com.example.demo.controller;

import com.example.demo.role.RoleService;
import com.example.demo.user.User;
import com.example.demo.user.UserService;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final RoleService roleService;

    @GetMapping("list")
    public List<UserRepresentation> findAll() {
        return userService.findAllUsers();
    }

    @GetMapping("/{id}")
    public UserRepresentation findById(@PathVariable String id) {
        return userService.findById(id);
    }

    @GetMapping("/username/{username}")
    public List<UserRepresentation> findByUsername(@PathVariable String username) {
        return userService.findByUsername(username);
    }

    @PostMapping("")
    public ResponseEntity<URI> createUser(@RequestBody User newUser) {
        Response response = userService.createUser(newUser);

        if (response.getStatus() != 201)
            throw new RuntimeException("User was not created");

        return ResponseEntity.created(response.getLocation()).build();
    }

    @PostMapping("/{userId}/role/{roleName}")
    public void assignRole(@PathVariable String userId,
                           @PathVariable String roleName) {
        RoleRepresentation role = roleService.findRoleByName(roleName);
        userService.assignRole(userId, role);
    }

    @PutMapping("/{userId}")
    public void updateUser(
            @PathVariable("userId") String userId,
            @RequestBody User newUser) {
        userService.updateUser(userId, newUser);

    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
    }

}
