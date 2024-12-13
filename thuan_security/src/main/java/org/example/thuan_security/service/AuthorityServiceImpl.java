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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthorityServiceImpl implements AuthorityService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionsRepository permissionsRepository;
    private PermissionRoleRepository permissionRoleRepository;
    @Override
    public UserAuthority getUserAuthority(UUID userId) {

        return null;
    }

    @Override
    public UserAuthority getUserAuthority(String username) {
        Users users=userRepository.findByEmail(username);
        Set<String> roles = users.getRoles();
        String role =null;// Tên claim tùy thuộc vào cách bạn đặt trong JWT
        if (roles != null && !roles.isEmpty()) {
            role = roles.iterator().next(); // Lấy phần tử đầu tiên trong Set
        }
        Roles userRole=roleRepository.findByName(role);
        List<PermissionRole> rolePermissions = permissionRoleRepository.findAllByRoleId(userRole.getId());
        log.info("---USER GRANT---" + mapRolesToAuthorities(userRole, rolePermissions).toString());

        return UserAuthority.builder()
                .userId(UUID.fromString(users.getUserId()))
                .email(users.getEmail())
                .deleted(users.isDeleted())
                .verified(users.isVerified())
                .enabled(users.isEnabled())
                .password(users.getPassword())
                .grantedPermissions(mapRolesToAuthorities(userRole, rolePermissions))
                .build();
    }

    private List<String> mapRolesToAuthorities(Roles roles, List<PermissionRole> rolePermissions) {
        // Kiểm tra nếu rolePermissions không rỗng
        if (rolePermissions == null || rolePermissions.isEmpty()) {
            throw new IllegalArgumentException("Role permissions cannot be empty");
        }

        // Lấy tên role từ đối tượng Roles
        String roleAuthorities = roles.getName();

        // Lấy Permission từ permissionRepository
        Permissions permissions = permissionsRepository.findById(rolePermissions.get(0).getId()).orElse(null);

        // Kiểm tra nếu không tìm thấy permission
        if (permissions == null) {
            throw new IllegalArgumentException("Permission not found for ID: " + rolePermissions.get(0).getId());
        }

        // Tạo chuỗi permission
        String permission = permissions.getResource() + "." + permissions.getName();

        // Sử dụng Stream.concat để kết hợp các quyền và role
        return Stream.concat(Stream.of(roleAuthorities), Stream.of(permission)) // Chuyển các chuỗi thành Stream
                .collect(Collectors.toList()); // Chuyển kết quả thành List
    }


    @Override
    public UserAuthority getClientAuthority(UUID clientId) {
        return null;
    }
}
