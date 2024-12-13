package org.example.storage_service.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)

public class FileSearchRequest extends PagingRequest {
    String keyword;
    String fileType;
    String fileName;
    String sortBy;

}
