package com.example.demo.controller;

import com.example.demo.role.RoleService;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/role")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;

    @GetMapping
    public List<RoleRepresentation> findAll() {
        return roleService.findAllRole();
    }

    @PostMapping
    public void createRole(@RequestParam String name) {
        roleService.createRole(name);
    }

}
