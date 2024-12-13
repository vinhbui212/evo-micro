package org.example.thuan_security.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.thuan_security.model.Permissions;
import org.example.thuan_security.model.Roles;
import org.example.thuan_security.repository.PermissionsRepository;
import org.example.thuan_security.request.PermissionRequest;
import org.example.thuan_security.request.SearchRequest;
import org.example.thuan_security.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.security.Permission;
import java.util.List;

import static org.example.thuan_security.service.user.UserServiceImpl.createPageable;

@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionService {

    private final PermissionsRepository permissionRepository;

    public Permissions createPermission(PermissionRequest permissionRequest) {
        Permissions permission = new Permissions();
        permission.setResource(permissionRequest.getResource());
        permission.setScope(permissionRequest.getScope());
        permission.setName(permissionRequest.getName());

        return permissionRepository.save(permission);
    }

    public Permissions updatePermission(Long id, PermissionRequest permissionRequest) {
        Permissions permission = permissionRepository.findById(id).orElseThrow(() -> new RuntimeException("Permission not found"));
        permission.setName(permissionRequest.getName());
        permission.setScope(permissionRequest.getScope());
        permission.setResource(permissionRequest.getResource());
        log.info(permission.toString());
        return permissionRepository.save(permission);
    }

    public void deletePermission(Long id) {
        permissionRepository.deleteById(id);
    }

    public Page<Permissions> getAllPermissions(SearchRequest searchRequest) {
        Pageable sortedPageable = createPageable(searchRequest);

        Page<Permissions> permissions = permissionRepository.findAll(sortedPageable);
        if (permissions.isEmpty()) {
            System.out.println("No users found.");
        }
        return permissions;
    }
}

