package org.example.thuan_security.service;

import com.evo.common.UserAuthority;
import com.evo.common.webapp.security.AuthorityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.thuan_security.model.PermissionRole;
import org.example.thuan_security.model.Permissions;
import org.example.thuan_security.model.Roles;
import org.example.thuan_security.model.Users;
import org.example.thuan_security.repository.PermissionRoleRepository;
import org.example.thuan_security.repository.PermissionsRepository;
import org.example.thuan_security.repository.RoleRepository;
import org.example.thuan_security.repository.UserRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
@Primary
public class AuthorityServiceImpl implements AuthorityService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionsRepository permissionsRepository;
    private final PermissionRoleRepository permissionRoleRepository;
    @Override
    public UserAuthority getUserAuthority(UUID userId) {

        return null;
    }

    @Override
    public UserAuthority getUserAuthority(String username) {
        Users users=userRepository.findByEmail(username);

        Set<String> roles = users.getRoles();
        String role =null;
        if (roles != null && !roles.isEmpty()) {
            role = roles.iterator().next(); // Lấy phần tử đầu tiên trong Set
        }
        Roles userRole=roleRepository.findByName(role);
        log.info(userRole.getId().toString());
        List<PermissionRole> rolePermissions = permissionRoleRepository.findAllByRoleId((userRole.getId()));
        log.info(rolePermissions.toString());
        log.info("---USER GRANT---" + mapRolesToAuthorities(userRole, rolePermissions).toString());

        return UserAuthority.builder()
                .userId((users.getId()))
                .email(users.getEmail())
                .deleted(users.isDeleted())
                .verified(users.isVerified())
                .enabled(users.isEnabled())
                .password(users.getPassword())
                .grantedPermissions(mapRolesToAuthorities(userRole, rolePermissions))
                .isRoot(false)
                .build();
    }

    private List<String> mapRolesToAuthorities(Roles roles, List<PermissionRole> rolePermissions) {
        // Kiểm tra nếu rolePermissions không rỗng
        if (rolePermissions == null || rolePermissions.isEmpty()) {
            throw new IllegalArgumentException("Role permissions cannot be empty");
        }

        // Lấy tên role từ đối tượng Roles
        String roleAuthorities = roles.getName();

        // Lấy danh sách ID từ rolePermissions
        List<Long> permissionIds = rolePermissions.stream()
                .map(PermissionRole::getPermissionId)
                .collect(Collectors.toList());

        // Lấy danh sách Permission từ repository
        List<Permissions> permissions = permissionsRepository.findAllById(permissionIds);
        log.info("Permissions: " + permissions);

        // Kiểm tra nếu không tìm thấy permission nào
        if (permissions == null || permissions.isEmpty()) {
            throw new IllegalArgumentException("Permissions not found for IDs: " + permissionIds);
        }

        // Tạo danh sách permission từ resource và scope
        List<String> permissionStrings = permissions.stream()
                .map(p -> p.getResource() + "." + p.getScope())
                .collect(Collectors.toList());
        log.info("Permission Strings: " + permissionStrings);

        // Kết hợp roleAuthorities và danh sách permissionStrings
        return Stream.concat(Stream.of(roleAuthorities), permissionStrings.stream())
                .collect(Collectors.toList());
    }


    @Override
    public UserAuthority getClientAuthority(UUID clientId) {
        return null;
    }
}
