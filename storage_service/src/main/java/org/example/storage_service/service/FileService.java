package org.example.storage_service.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.example.storage_service.dto.request.FileSearchRequest;
import org.example.storage_service.dto.response.FileResponse;
import org.example.storage_service.entity.File;
import org.example.storage_service.repository.FileRepository;
import org.example.storage_service.repository.FileRepositoryCustom;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FileService {

    private final org.example.storage_service.repository.FileRepository fileRepository;
    //    private final FileRepositoryImpl fileRepositoryImpl;
    private final FileRepositoryCustom fileRepositoryCustom;
    private String uploadDir = "uploads";

    public org.example.storage_service.entity.File uploadFile(MultipartFile file, boolean visibility, String version, Long userId) throws IOException {
        String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        Path uploadPath = Paths.get(uploadDir, currentDate);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        String fileName = file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath);

        return fileRepository.save(org.example.storage_service.entity.File.builder()
                .fileName(fileName)
                .ownerId(userId)
                .fileType(file.getContentType())
                .fileSize(String.valueOf(file.getSize()))
                .filePath(filePath.toString())
                .visible(visibility)
                .build());
    }

    public List<File> uploadFiles(List<MultipartFile> files, boolean visibility, String version, Long userId) throws IOException {
        if (files.isEmpty()) {
            return new ArrayList<>(); // Return an empty list if no files are provided
        }

        List<File> uploadedFiles = new ArrayList<>();
        for (MultipartFile file : files) {
            try {
                // Upload a single file and get the File entity returned from uploadFile
                File uploadedFile = uploadFile(file, visibility, version, userId);
                uploadedFiles.add(uploadedFile); // Add the uploaded file to the list
            } catch (IOException e) {
                // Handle the error, e.g., log it
                System.err.println("Error uploading file " + file.getOriginalFilename() + ": " + e.getMessage());
                // Optionally, rethrow the exception or continue based on your requirements
            }
        }
        return uploadedFiles; // Return the list of uploaded files
    }


    public Resource getFile(Long fileId) throws IOException {
        org.example.storage_service.entity.File fileEntity = fileRepository.findById(fileId).orElseThrow(() -> new RuntimeException("File not found"));
        Path path = Paths.get(fileEntity.getFilePath());
        Resource resource = new FileSystemResource(path);
        if (!resource.exists()) {
            throw new IOException("File not found: " + fileEntity.getFileName());
        }
        return resource;
    }

    public byte[] processImage(Resource resource, Optional<Integer> width, Optional<Integer> height, Optional<Double> ratio) throws IOException {
        String contentType = Files.probeContentType(Paths.get(resource.getURI()));
        if (contentType != null && contentType.startsWith("image")) {
            InputStream inputStream = resource.getInputStream();
            Thumbnails.Builder<?> thumbnailBuilder = Thumbnails.of(inputStream);
            if (width.isPresent() && height.isPresent()) {
                thumbnailBuilder.forceSize(width.get(), height.get());
            } else if (ratio.isPresent()) {
                thumbnailBuilder.scale(ratio.get());
            }
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            thumbnailBuilder.toOutputStream(outputStream);

            return outputStream.toByteArray();
        }
        return Files.readAllBytes(Paths.get(resource.getURI()));
    }

    public org.example.storage_service.entity.File updateFile(Long fileId, MultipartFile file, boolean visibility, String version) throws IOException {
        org.example.storage_service.entity.File existingFile = fileRepository.findById(fileId).orElseThrow(() -> new RuntimeException("File not exist"));
        Path uploadPath = Paths.get(uploadDir);
        String fileName = file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);
        if (Files.exists(Paths.get(existingFile.getFilePath()))) {
            Files.delete(Paths.get(existingFile.getFilePath()));
        }
        Files.copy(file.getInputStream(), filePath);
        existingFile.setFileName(fileName);
        existingFile.setFileType(file.getContentType());
        existingFile.setFileSize(String.valueOf(file.getSize()));
        existingFile.setFilePath(filePath.toString());
        existingFile.setVisible(visibility);

        return fileRepository.save(existingFile);
    }

    public void deleteFile(Long fileId) throws IOException {
        org.example.storage_service.entity.File file = fileRepository.findById(fileId).orElseThrow(() -> new RuntimeException("File not found"));
        Path filePath = Paths.get(file.getFilePath());
        file.setDeleted(true);
        fileRepository.save(file);
        if (Files.exists(filePath)) {
            Files.delete(filePath);
        } else {
            throw new IOException("File not found");
        }
    }

    public Page<File> searchFiles(FileSearchRequest request) {
        // Gọi repository để thực hiện tìm kiếm
        return fileRepositoryCustom.search(request);
    }

//    public FilesResponse<File> getFile(FileSearchRequest request) {
//        List<File> file = fileRepositoryImpl.searchFile(request);
//        Long totalFile = fileRepositoryImpl.countFile(request);
//        return new FilesResponse<>(file, (totalFile / request.getSize()));
//    }
}