package org.example.thuan_security.request;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.A;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSearchRequest extends PagingRequest {
   private String keyword;
   private String sortBy;
   private String email;
   private String createAt;
}
