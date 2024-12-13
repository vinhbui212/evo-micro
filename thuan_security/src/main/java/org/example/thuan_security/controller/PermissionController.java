package org.example.thuan_security.controller;

import lombok.RequiredArgsConstructor;
import org.example.thuan_security.model.Permissions;
import org.example.thuan_security.request.PermissionRequest;
import org.example.thuan_security.request.SearchRequest;
import org.example.thuan_security.service.PermissionService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @PreAuthorize("hasPermission('permission','CREATE')")
    @PostMapping
    public ResponseEntity<Permissions> createPermission(@RequestBody PermissionRequest permissionRequest) {
        Permissions permission = permissionService.createPermission(permissionRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(permission);
    }

    @PreAuthorize("hasPermission('permission','UPDATE')")
    @PutMapping("/{id}")
    public ResponseEntity<Permissions> updatePermission(@PathVariable Long id, @RequestBody PermissionRequest permissionRequest) {
        Permissions updatedPermission = permissionService.updatePermission(id, permissionRequest);
        return ResponseEntity.ok(updatedPermission);
    }

    @PreAuthorize("hasPermission('permission','DELETE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePermission(@PathVariable Long id) {
        permissionService.deletePermission(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Permission deleted successfully");
    }

    @PreAuthorize("hasPermission('permission','READ')")
    @GetMapping
    public ResponseEntity<Page<Permissions>> getAllPermissions(@ParameterObject SearchRequest searchRequest) {
        Page<Permissions> permissions = permissionService.getAllPermissions(searchRequest);
        return ResponseEntity.ok(permissions);
    }


}

