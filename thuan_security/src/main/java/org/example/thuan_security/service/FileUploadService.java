package org.example.thuan_security.service;


import com.evo.common.client.storage.StorageClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.thuan_security.model.Users;
import org.example.thuan_security.repository.UserRepository;
import org.example.thuan_security.response.StorageResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileUploadService {

    private static final String uploadDir = "/uploads/";
    private final UserRepository userRepository;
    private final StorageClient storageClient;
    public String uploadFile(MultipartFile file, String email) throws Exception {
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                throw new IOException("Không thể tạo thư mục uploadDir");
            }
        }

        Users user = userRepository.findByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("Không tìm thấy người dùng với email: " + email);
        }

        if (user.getImage_url() != null) {
            String oldFilePath = user.getImage_url().replace("http://localhost:8081/api/files/images/", uploadDir);
            File oldFile = new File(oldFilePath);
            if (oldFile.exists() && !oldFile.delete()) {
                log.warn("Không thể xóa file cũ: " + oldFilePath);
            }
        }

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir + fileName);
        Files.write(filePath, file.getBytes());
        log.info("File saved at: " + filePath.toString());

        String imageUrl = "http://localhost:8081/api/files/images/" + fileName;
        user.setImage_url(imageUrl);
        userRepository.save(user);

        return imageUrl;
    }

    public byte[] getImage(String filename) throws IOException {
        Path filePath = Paths.get(uploadDir + filename);
        if (!Files.exists(filePath)) {
            throw new IOException("Không tìm thấy file");
        }
        return Files.readAllBytes(filePath);
    }



    public String extractFilename(StorageResponse response) {
        // Kiểm tra xem response có chứa dữ liệu không
        if (response.getData() != null) {
            // Lấy tên file của đối tượng đầu tiên trong danh sách
            return (String) response.getData();
        } else {
            // Nếu không có dữ liệu hoặc danh sách trống, trả về null hoặc thông báo lỗi
            return null; // Hoặc có thể ném Exception tùy theo yêu cầu của bạn
        }
    }

}
