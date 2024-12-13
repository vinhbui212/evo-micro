package org.example.thuan_security.controller;

import org.example.thuan_security.model.Users;
import org.example.thuan_security.service.excel.ExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/excel")
public class ExcelController {

    @Autowired
    private ExcelService userService;

    // API để export người dùng ra file Excel với filter
    @GetMapping("/exportUsers")
    public ResponseEntity<byte[]> exportUsersToExcel(@RequestParam(required = false) String email,
                                                     @RequestParam(required = false) Boolean verified,
                                                     @RequestParam(required = false) String role) throws IOException {
        // Lấy dữ liệu từ service
        byte[] excelData = userService.exportUsersToExcel(email, verified, role);

        // Tạo header cho file Excel
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=users.xlsx");
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);

        // Trả về file Excel dưới dạng byte array
        return new ResponseEntity<>(excelData, headers, HttpStatus.OK);
    }

    @PostMapping("/importUsers")
    public ResponseEntity<?> importUsers(@RequestParam("file") MultipartFile file) {
        try {
            List<String> errorMessages = userService.importUsersFromExcel(file);
            if (!errorMessages.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessages);
            }
            return ResponseEntity.ok("Import dữ liệu thành công.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Có lỗi xảy ra khi xử lý file.");
        }
    }
}

