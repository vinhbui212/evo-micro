package com.evo.common.client.storage;

import com.evo.common.client.iam.IamClientFallback;
import com.evo.common.config.FeignClientConfiguration;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;


@FeignClient(
        url = "http://localhost:8082",
        name = "storage-client",
        contextId = "common-storage",
        configuration = FeignClientConfiguration.class,
        fallbackFactory = IamClientFallback.class)
public interface StorageClient {

    @PostMapping(value = "/api/private/files/uploadMultiple", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @LoadBalanced
    ResponseEntity<?> uploadToStorage(
            @RequestPart("file") List<MultipartFile> file,
            @RequestParam("visibility") boolean visibility,
            @RequestParam("version") String version,
            @RequestParam("userId") Long userId
    );

    @GetMapping(value = "/api/private/files/getImage")
    @LoadBalanced
    ResponseEntity<byte[]> getImage(
            @RequestParam Optional<Integer> width,
            @RequestParam Optional<Integer> height,
            @RequestParam Optional<Double> ratio,
            @RequestParam Long ownerId
    );
}