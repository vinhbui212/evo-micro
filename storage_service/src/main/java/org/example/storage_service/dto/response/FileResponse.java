package org.example.storage_service.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)

public class FileResponse {
    private Long id;
    private String fileName;
    private String fileType;
    private String filePath;
    public FileResponse(Long id, String fileName, String fileType, String filePath) {
        this.id=id;
        this.fileName = fileName;
        this.fileType = fileType;
        this.filePath = filePath;
    }
}
