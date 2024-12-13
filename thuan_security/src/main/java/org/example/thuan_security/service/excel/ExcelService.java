package org.example.thuan_security.service.excel;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.thuan_security.model.Users;
import org.example.thuan_security.repository.UserRepository;
import org.example.thuan_security.response.UserImportDTO;
import org.example.thuan_security.response.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;


@Service

public class ExcelService {
    @Autowired
    private  UserRepository userRepository;
    @Autowired
    private UserValidator userValidator;



    // Phương thức để lấy tất cả người dùng từ DB và xuất ra file Excel
    public byte[] exportUsersToExcel(String emailFilter, Boolean verifiedFilter, String roleFilter) throws IOException {
        // Lấy danh sách người dùng từ DB (có thể áp dụng filter nếu cần)
        List<Users> users = userRepository.findAll();
        if (emailFilter != null && !emailFilter.isEmpty()) {
            users = userRepository.findByEmailContainingIgnoreCase(emailFilter);  // Lọc theo email
        } else if (verifiedFilter != null) {
            users = userRepository.findByVerified(verifiedFilter);  // Lọc theo trạng thái verified
        }  // Lọc theo vai trò
         else {
            users = userRepository.findAll();  // Nếu không có filter thì lấy tất cả
        }
        // Tạo workbook và sheet Excel
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Users");

        // Tạo style cho tiêu đề (bôi đậm và tô màu xanh dương)
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);
        headerCellStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerCellStyle.setAlignment(HorizontalAlignment.CENTER);

        // Tạo tiêu đề
        Row headerRow = sheet.createRow(0);
        String[] columns = {"ID", "Email", "UserId", "Full Name", "Date of Birth", "Verified", "Roles"};
        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerCellStyle);  // Áp dụng style cho tiêu đề
        }

        // Tạo các row dữ liệu
        int rowNum = 1;
        for (Users user : users) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(user.getId());
            row.createCell(1).setCellValue(user.getEmail());
            row.createCell(2).setCellValue(user.getUserId());
            row.createCell(3).setCellValue(user.getFullName());
            row.createCell(4).setCellValue(user.getDob() != null ? user.getDob().toString() : "");
            row.createCell(5).setCellValue(user.isVerified());
            row.createCell(6).setCellValue(String.join(", ", user.getRoles()));
        }

        // Tự động điều chỉnh độ rộng cột
        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Chuyển dữ liệu Excel thành byte array để trả về
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        workbook.write(byteArrayOutputStream);
        workbook.close();  // Đóng workbook

        return byteArrayOutputStream.toByteArray();
    }

    public List<String> importUsersFromExcel(MultipartFile file) throws IOException {
        List<String> errorMessages = new ArrayList<>();
        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);  // Lấy sheet đầu tiên trong file Excel


        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);

            // Đọc các giá trị từ file Excel
            String email = getCellValue(row, 0);
            String fullName = getCellValue(row, 1);
            String dobStr = getCellValue(row, 2);
            String rolesStr = getCellValue(row, 3);

            // Tạo DTO UserImportDTO và gán giá trị
            UserImportDTO userDTO = new UserImportDTO();
            userDTO.setEmail(email);
            userDTO.setFullName(fullName);
            userDTO.setDob(LocalDate.parse(dobStr));
            userDTO.setRoles(new HashSet<>(Arrays.asList(rolesStr.split(","))));

            // Tạo đối tượng Errors để chứa thông tin lỗi
            Errors errors = new org.springframework.validation.BindException(userDTO, "userImportDTO");

            // Validate dữ liệu
            userValidator.validate(userDTO, errors);

            // Kiểm tra lỗi và lưu thông báo
            if (errors.hasErrors()) {
                for (org.springframework.validation.ObjectError error : errors.getAllErrors()) {
                    errorMessages.add("Dòng " + (i + 1) + ": " + error.getDefaultMessage());
                }
            } else {
                // Nếu không có lỗi, lưu người dùng vào DB
                if (!userRepository.existsByEmail(email)) {
                    Users user = new Users();
                    user.setEmail(userDTO.getEmail());
                    user.setFullName(userDTO.getFullName());
                    user.setDob(userDTO.getDob());
                    user.setRoles(userDTO.getRoles());
                    userRepository.save(user);
                } else {
                    errorMessages.add("Dòng " + (i + 1) + ": Email '" + email + "' đã tồn tại.");
                }
            }
        }

        return errorMessages;
    }

    // Phương thức để lấy giá trị từ các ô trong Excel (cell)
    private String getCellValue(Row row, int columnIndex) {
        Cell cell = row.getCell(columnIndex);
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            default:
                return "";
        }
    }
}
