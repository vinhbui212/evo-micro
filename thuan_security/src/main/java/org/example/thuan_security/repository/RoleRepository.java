package org.example.thuan_security.repository;

import org.example.thuan_security.model.Roles;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Roles,Long> {
    Roles findByName(String name);
}
