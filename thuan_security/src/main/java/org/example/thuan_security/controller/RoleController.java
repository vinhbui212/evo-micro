package org.example.thuan_security.controller;


import lombok.RequiredArgsConstructor;
import org.example.thuan_security.model.Permissions;
import org.example.thuan_security.model.Roles;
import org.example.thuan_security.model.Users;
import org.example.thuan_security.request.RoleRequest;
import org.example.thuan_security.request.SearchRequest;
import org.example.thuan_security.service.RoleService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;


@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @PreAuthorize("hasPermission('role','CREATE')")
    @PostMapping
    public ResponseEntity<Roles> createRole(@RequestBody RoleRequest request) {
        try {
            Roles role = roleService.createRole(request);
            if(role != null) {
                return ResponseEntity.status(HttpStatus.CREATED).body(role);
            }
            else return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);

        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PreAuthorize("hasPermission('role','UPDATE')")
    @PutMapping("/{id}")
    public ResponseEntity<Roles> updateRole(@PathVariable Long id, @RequestBody RoleRequest request) {
        Roles updatedRole = roleService.updateRole(id, request);
        return ResponseEntity.ok(updatedRole);
    }

    @PreAuthorize("hasPermission('role','DELETE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Role deleted successfully");
    }

    @PreAuthorize("hasPermission('role','UPDATE')")
    @PostMapping("/assign-permissions")
    public ResponseEntity<String> assignPermissionsToRole(
            @RequestParam Long roleId, @RequestParam Long permissionId) {
        roleService.assignPermissionsToRole(roleId, permissionId);
        return ResponseEntity.ok("Role "+ roleId +"assigned successflly permission " + permissionId);
    }

    @PreAuthorize("hasPermission('role','UPDATE')")
    @PostMapping("/{userId}/roles/{roleId}")
    public ResponseEntity<String> assignRoletoUser(
            @PathVariable Long userId,
            @PathVariable Long roleId) {
        roleService.assignRoleToUser(userId, roleId);
        return ResponseEntity.ok("Role assigned to role successfully");
    }

    @PreAuthorize("hasPermission('role','READ')")
    @GetMapping
    public ResponseEntity<Page<Roles>> getAllRoles(@ParameterObject SearchRequest searchRequest) {
        Page<Roles> permissions = roleService.getAllRoles(searchRequest);
        return ResponseEntity.ok(permissions);
    }
}


