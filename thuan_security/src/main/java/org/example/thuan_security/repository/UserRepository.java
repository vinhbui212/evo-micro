package org.example.thuan_security.repository;

import org.example.thuan_security.model.Users;
import org.example.thuan_security.request.SearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface UserRepository extends JpaRepository<Users,Long>, UserRepositoryCustom  {
    Users findByEmail(String email);
    Users findByUserId(String userId);
    boolean existsByUserId(String userId);
    List<Users> findAll();  // Tìm tất cả người dùng
    List<Users> findByEmailContainingIgnoreCase(String email);

    // Lọc người dùng theo trạng thái verified
    List<Users> findByVerified(boolean verified);



    @Query(value = "SELECT * FROM tbl_user u WHERE similarity(unaccent(u.full_name), unaccent(:keyword)) > 0.3", nativeQuery = true)
    Page<Users> findByKeyword(@Param("keyword") String keyword, Pageable pageable);


    boolean existsByEmail(String email);
}
