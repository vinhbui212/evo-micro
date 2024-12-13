package org.example.thuan_security.repository;

import org.example.thuan_security.model.Users;
import org.example.thuan_security.request.UserSearchRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserRepositoryCustom {
    Page<Users> search(UserSearchRequest request);

    Long count(UserSearchRequest request);
}
