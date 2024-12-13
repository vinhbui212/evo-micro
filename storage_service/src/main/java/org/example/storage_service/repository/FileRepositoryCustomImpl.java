package org.example.storage_service.repository;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.storage_service.dto.request.FileSearchRequest;
import org.example.storage_service.entity.File;
import org.example.storage_service.repository.FileRepositoryCustom;
import org.example.storage_service.utils.SqlUtils;

import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Primary
public class FileRepositoryCustomImpl implements FileRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<File> search(FileSearchRequest request) {
        Map<String, Object> values = new HashMap<>();
        String sql = "select e from File e " + createWhereQuery(request, values) + createOrderQuery(request.getSortBy());
        log.info(sql);

        // Tạo query và thiết lập các tham số
        Query query = entityManager.createQuery(sql, File.class);
        log.info(query.getResultList().toString());
        values.forEach(query::setParameter);

        // Thiết lập phân trang
        query.setFirstResult(request.getPage() * request.getSize());
        query.setMaxResults(request.getSize());

        // Lấy danh sách kết quả
        List<File> resultList = query.getResultList();
        log.info(String.valueOf(resultList));

        // Trả về một Page<Users> sử dụng PageImpl
        long totalRecords = count(request); // Hàm này sẽ trả về tổng số bản ghi trong database
        return new PageImpl<>(resultList, PageRequest.of(request.getPage(), request.getSize()), totalRecords);
    }

    @Override
    public Long count(FileSearchRequest request) {
        Map<String, Object> values = new HashMap<>();
        String sql = "select count(e) from File e " + createWhereQuery(request, values);
        Query query = entityManager.createQuery(sql, Long.class);
        values.forEach(query::setParameter);
        return (Long) query.getSingleResult();
    }

    private String createWhereQuery(FileSearchRequest request, Map<String, Object> values) {
        StringBuilder sql = new StringBuilder();
        sql.append(" where e.deleted = false");
        if (StringUtils.isNotBlank(request.getKeyword())) {
            sql.append(
                    " and ( lower(e.fileType) like :keyword" +
                            " or lower(e.fileName) like :keyword");
            values.put("keyword", SqlUtils.encodeKeyword(request.getKeyword()));
        }

        if (StringUtils.isNotBlank(request.getFileType())) {
            sql.append(" and lower(e.fileType) like :fileType ");
            values.put("fileType", SqlUtils.encodeKeyword(request.getFileType()));
        }

        return sql.toString();
    }

    public StringBuilder createOrderQuery(String sortBy) {
        StringBuilder hql = new StringBuilder(" ");
        if (org.springframework.util.StringUtils.hasLength(sortBy)) {
            hql.append(" order by e.").append(sortBy.replace(".", " "));
        } else {
            hql.append(" order by e.lastModifiedAt desc ");
        }
        return hql;
    }
}
