package org.example.thuan_security.controller;

import com.evo.common.client.storage.StorageClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.thuan_security.model.Users;
import org.example.thuan_security.repository.UserRepository;
import org.example.thuan_security.response.ApiResponse2;
import org.example.thuan_security.response.StorageResponse;
import org.example.thuan_security.service.FileUploadService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Slf4j
public class FileUploadController {

    private final FileUploadService fileUploadService;
    private final StorageClient storageClient;
    private final UserRepository userRepository;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestBody MultipartFile file, @RequestParam("email") String email) {
        try {
            String imageUrl = fileUploadService.uploadFile(file, email);
            Users users=userRepository.findByEmail(email);
            storageClient.uploadToStorageSingle(file,true,"1", users.getId());
            return ResponseEntity.ok("File uploaded and updated successfully: " + imageUrl);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lỗi: " + e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Không thể tải ảnh lên");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Lỗi: " + e.getMessage());
        }
    }

    @GetMapping("/images/{filename}")
    public ResponseEntity<byte[]> getImage(@PathVariable String filename) {
        try {
            byte[] image = fileUploadService.getImage(filename);
            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(image);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/uploadStorage")
    public ResponseEntity<?> uploadFileStorage(@RequestParam("file") List<MultipartFile> file,
                                               @RequestParam("visibility") boolean visibility,
                                               @RequestParam("version") String version,
                                               @RequestParam("userId") Long userId) {
        try {
            ResponseEntity<?> imageUrl = storageClient.uploadToStorage(file, visibility, version, userId);
            return ResponseEntity.ok().body(ApiResponse2.builder()
                    .code(1)
                    .message("Sucess")
                    .status(HttpStatus.CREATED.value())
                    .data(imageUrl)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Lỗi: " + e.getMessage());
        }
    }
    @GetMapping("/viewImage")
    public ResponseEntity<?> getImage(
            @RequestParam Optional<Integer> width,  // Optional width
            @RequestParam Optional<Integer> height,  // Optional height
            @RequestParam Optional<Double> ratio,    // Optional ratio
            @RequestParam Long ownerId               // ID của người sở hữu ảnh
    ) {
        try {
            // Gọi API từ StorageClient để lấy dữ liệu ảnh
            ResponseEntity<byte[]> imageResponse = storageClient.getImage(width, height, ratio, ownerId);

            // Trả về ảnh trực tiếp nếu thành công
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(imageResponse.getBody());
        } catch (Exception e) {
            // Xử lý lỗi nếu xảy ra vấn đề
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse2.builder()
                    .code(0)
                    .message("Lỗi: " + e.getMessage())
                    .status(HttpStatus.BAD_REQUEST.value())
                    .build());
        }
    }

    @GetMapping("/getFileStorage/{fileId}")
    public ResponseEntity<Resource> getFile(@PathVariable Long fileId) throws IOException {

        return storageClient.getFile(fileId);
    }

}
