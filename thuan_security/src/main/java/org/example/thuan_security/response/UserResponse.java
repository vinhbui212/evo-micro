package org.example.thuan_security.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)

public class UserResponse {
    Long id;
    @NotBlank(message = "Full name cannot be null or empty")
    String fullName;
    String lastName;
    String firstName;
    String email;
    String role;
    String imgUrl;

    public UserResponse(Long id, String fullName, String email, Set<String> roles, String imageUrl) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.role = roles.toString();
        this.imgUrl = imageUrl;

    }
}
