//package org.example.thuan_security.config;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.example.thuan_security.model.Permissions;
//import org.example.thuan_security.model.Roles;
//import org.example.thuan_security.model.Users;
//import org.example.thuan_security.repository.PermissionRoleRepository;
//import org.example.thuan_security.repository.PermissionsRepository;
//import org.example.thuan_security.repository.RoleRepository;
//import org.example.thuan_security.repository.UserRepository;
//import org.springframework.security.access.PermissionEvaluator;
//import org.springframework.security.core.Authentication;
//import org.springframework.stereotype.Component;
//
//import java.io.Serializable;
//import java.util.List;
//import java.util.Set;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class CustomPermissionEvaluator implements PermissionEvaluator {
//
//    private final UserRepository accountRepository;
//    private final PermissionsRepository permissionRepository;
//    private final PermissionRoleRepository permissionRoleRepository;
//    private final UserRepository userRepository;
//    private final RoleRepository roleRepository;
//
//    @Override
//    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
//
//        return checkUserPermission(authentication.getName(), targetDomainObject, permission);
//    }
//
//    @Override
//    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
//        return checkUserPermission(authentication.getName(), targetId, permission);
//    }
//
//    private boolean checkUserPermission(String email, Object resource, Object scope) {
//        Users users = userRepository.findByEmail(email);
//        Set<String> rolename = users.getRoles();
//        String roleFound = rolename.iterator().next();
//        if (roleFound.equals("ROLE_SUPERADMIN")) {
//            return true;
//        } else {
//            Roles roles = roleRepository.findByName(roleFound);
//            Permissions permissions = permissionRepository.findByScopeAndResource(scope.toString(),resource.toString());
//
//            boolean ok = permissionRoleRepository.existsByRoleIdAndPermissionId(roles.getId().toString(), permissions.getId().toString());
//
//            return ok;
//        }
//    }
//
//}
//
//
////@Component
////@RequiredArgsConstructor
////public class CustomPermissionEvaluator implements PermissionEvaluator {
////
////    private final AccountRepository accountRepository;
////    private final PermissionRepository permissionRepository;
////    private final RolePermissionRepository rolePermissionRepository;
////
////    @Override
////    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
////
////        if(targetDomainObject instanceof String resourceName) {
////            Permission foundResource = permissionRepository.findByNameIgnoreCase(resourceName)
////                    .orElseThrow(()-> new AppExceptions(ErrorCode.PERMISSION_NOTFOUND));
////            return checkUserPermission(authentication.getName(), foundResource.getId(), permission);
////        }
////        return false;
////    }
////
////    @Override
////    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
////        return checkUserPermission(authentication.getName(), targetId, permission);
////    }
////
////    private boolean checkUserPermission(String email, Object resourceId, Object permissionScope) {
////        PermissionScope foundScope = PermissionScope.valueOf(permissionScope.toString());
////
////        Account foundUser = accountRepository.findByEmail(email)
////                .orElseThrow(() -> new AppExceptions(ErrorCode.NOTFOUND_EMAIL));
////
////        boolean res =  rolePermissionRepository
////                .existsByRoleIdAndPermissionIdAndScope(
////                        foundUser.getRoleId(),
////                        resourceId.toString(),
////                        foundScope
////                );
////
////        return res;
////    }
////}
//
