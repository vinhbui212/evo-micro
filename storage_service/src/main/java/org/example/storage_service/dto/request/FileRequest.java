package org.example.storage_service.dto.request;


import lombok.*;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileRequest {
    private String ratio;
    private int ownerId;

}
