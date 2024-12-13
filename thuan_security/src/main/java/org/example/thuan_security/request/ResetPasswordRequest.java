package org.example.thuan_security.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class ResetPasswordRequest {
    String type;
    String value;
    boolean temporary;

    public ResetPasswordRequest(String type, String value, boolean temporary) {
        this.type = type;
        this.value = value;
        this.temporary = temporary;
    }

    public ResetPasswordRequest(String value) {
        this.value = value;
    }
}
