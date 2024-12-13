package org.example.thuan_security.service.excel;

import org.example.thuan_security.response.UserImportDTO;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Service
public class UserValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        // Xác định lớp cần validate
        return UserImportDTO.class.equals(clazz);  // Giả sử chúng ta validate UserImportDTO
    }

    @Override
    public void validate(Object target, Errors errors) {
        // Kiểm tra các điều kiện validate ở đây
        UserImportDTO user = (UserImportDTO) target;

        // Kiểm tra userId không rỗng


        // Kiểm tra email hợp lệ
        if (user.getEmail() == null || !user.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            errors.rejectValue("email", "email.invalid", "Email is not valid");
        }

        // Kiểm tra fullName không trống
        if (user.getFullName() == null || user.getFullName().isEmpty()) {
            errors.rejectValue("fullName", "fullName.empty", "Full name cannot be empty");
        }

        // Kiểm tra ngày sinh
        if (user.getDob() == null) {
            errors.rejectValue("dob", "dob.null", "Date of birth cannot be null");
        }

        // Kiểm tra vai trò (Nếu cần)
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            errors.rejectValue("roles", "roles.empty", "At least one role is required");
        }
    }
}

