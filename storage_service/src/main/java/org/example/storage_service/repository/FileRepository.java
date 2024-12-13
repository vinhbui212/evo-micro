package org.example.storage_service.repository;

import org.example.storage_service.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File,Long> {
    File findByFileName(String fileName);

    File findByOwnerId(Long ownerId);
}
