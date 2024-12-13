package org.example.storage_service.controller;

import lombok.RequiredArgsConstructor;
import org.example.storage_service.dto.request.FileSearchRequest;
import org.example.storage_service.dto.response.ApiResponse;
import org.example.storage_service.dto.response.FileResponse;
import org.example.storage_service.repository.FileRepository;
import org.example.storage_service.service.FileService;
import org.example.storage_service.entity.File;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/private/files")
@RequiredArgsConstructor
public class FileControllerPrivate {

    private final FileService fileService;
    private final FileRepository fileRepository;


    @PostMapping("/upload")
    public ResponseEntity<ApiResponse> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("visibility") boolean visibility,
            @RequestParam("version") String version,
            @RequestParam("userId") Long userId
    ) {
        try {
            File uploadedFile = fileService.uploadFile(file, visibility, version,userId);
            return new ResponseEntity<>(ApiResponse.builder()
                    .status(200)
                    .message("Upload thành công")
                    .code(1)
                    .data(uploadedFile)
                    .build(), HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(ApiResponse.builder()
                    .status(500)
                    .message("Upload thất bại: " + e.getMessage())
                    .code(0)
                    .build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/uploadMultiple")
    public ResponseEntity<ApiResponse> uploadMultipleFiles(
            @RequestParam("file") List<MultipartFile> file,  // List of MultipartFile
            @RequestParam("visibility") boolean visibility,  // Tính năng visibility
            @RequestParam("version") String version,
            @RequestParam("userId") Long userId  // Phiên bản của file
    ) {
        try {
            List<File> result = (List<File>) fileService.uploadFiles(file, visibility, version,userId);
            return new ResponseEntity<>(ApiResponse.builder()
                    .status(200)
                    .message("Upload thành công")
                    .code(1)
                    .data(result)
                    .build(), HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(ApiResponse.builder()
                    .status(500)
                    .message("Upload thất bại: " + e.getMessage())
                    .code(0)
                    .build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get file by fileId
    @GetMapping("/getFile/{fileId}")
    public ResponseEntity<Resource> getFile(@PathVariable Long fileId) throws IOException {

        File file = fileRepository.findById(fileId).orElseThrow(() -> new RuntimeException("File not found"));

        Path filePath = Paths.get(file.getFilePath());

        if (!Files.exists(filePath)) {
            throw new IOException("File not found: " + file.getFileName());
        }

        Resource resource = new FileSystemResource(filePath);

        String contentType = Files.probeContentType(filePath);
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFileName() + "\"")
                .body(resource);
    }

    @GetMapping("/getImage")
    public ResponseEntity getImage(
            // Tên file
            @RequestParam Optional<Integer> width,  // Optional width
            @RequestParam Optional<Integer> height,  // Optional height
            @RequestParam Optional<Double> ratio,
            @RequestParam Long ownerId// Optional ratio
    ) {
        try {

            File file= fileRepository.findByOwnerId(ownerId);
            Resource resource=fileService.getFile(file.getId());
            byte[] imageBytes = fileService.processImage(resource, width, height, ratio);

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(imageBytes);
        } catch (IOException e) {
            return new ResponseEntity<>(ApiResponse.builder()
                    .status(500)
                    .message("Lỗi xử lý ảnh: " + e.getMessage())
                    .code(0)
                    .build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Update file information (change file)
    @PutMapping("/update/{fileId}")
    public ResponseEntity<ApiResponse> updateFile(
            @PathVariable Long fileId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("visibility") boolean visibility,
            @RequestParam("version") String version
    ) {
        try {
            File updatedFile = fileService.updateFile(fileId, file, visibility, version);
            return new ResponseEntity<>(ApiResponse.builder()
                    .status(200)
                    .message("File updated successfully")
                    .code(1)
                    .data(updatedFile)
                    .build(), HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(ApiResponse.builder()
                    .status(500)
                    .message("Lỗi cập nhật file: " + e.getMessage())
                    .code(0)
                    .build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Delete file
    @DeleteMapping("/delete/{fileId}")
    public ResponseEntity<ApiResponse> deleteFile(@PathVariable Long fileId) {
        try {
            fileService.deleteFile(fileId);
            return new ResponseEntity<>(ApiResponse.builder()
                    .status(200)
                    .message("File deleted successfully")
                    .code(1)
                    .build(), HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(ApiResponse.builder()
                    .status(500)
                    .message("Lỗi xóa file: " + e.getMessage())
                    .code(0)
                    .build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/search")
    public Page<File> searchUsers(
            @ParameterObject FileSearchRequest searchRequest) {

        return fileService.searchFiles(searchRequest);
    }
}
