package org.example.thuan_security.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class History extends AuditableEntity {
    @Id
    @GeneratedValue
    private Long id;
    private String fileName;
    private String action;
}
