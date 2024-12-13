package org.example.storage_service.repository;

import org.example.storage_service.dto.request.FileSearchRequest;
import org.example.storage_service.entity.File;
import org.springframework.data.domain.Page;

public interface FileRepositoryCustom {
    Page<File> search(FileSearchRequest request);

    Long count(FileSearchRequest request);
}
