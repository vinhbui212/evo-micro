package org.example.thuan_security.repository;

import org.example.thuan_security.model.Blacklist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface BlacklistRepository extends JpaRepository<Blacklist,Long> {
    Optional<Blacklist> findByToken(String token);

    void deleteByExpirationTimeBefore(LocalDateTime now);

}
