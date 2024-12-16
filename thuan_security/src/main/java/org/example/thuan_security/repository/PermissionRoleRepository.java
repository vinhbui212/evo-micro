package org.example.thuan_security.repository;

import org.example.thuan_security.model.PermissionRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;


public interface PermissionRoleRepository extends JpaRepository<PermissionRole, Long> {
     boolean existsByRoleIdAndPermissionId(String roleId, String permissionId);

     List<PermissionRole> findAllByRoleId(String id);
}
