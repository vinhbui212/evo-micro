//package org.example.thuan_security.controller.client;
//
//import org.example.thuan_security.response.ApiResponse;
//import org.example.thuan_security.response.ApiResponse2;
//import org.example.thuan_security.response.StorageResponse;
//import org.springframework.cloud.openfeign.FeignClient;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RequestPart;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.util.List;
//import java.util.Optional;
//
//@FeignClient(name = "storage-client", url = "${app.iam.internal-url}")
//public interface StorageClient {
//
//    @PostMapping(value = "/api/files/uploadMultiple", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    ResponseEntity<?> uploadToStorage(
//            @RequestPart("file") List<MultipartFile> file,
//            @RequestParam("visibility") boolean visibility,
//            @RequestParam("version") String version,
//            @RequestParam("userId") Long userId
//    );
//
//    @GetMapping(value = "/api/private/files/getImage")
//    ResponseEntity<byte[]> getImage(
//            @RequestParam Optional<Integer> width,
//            @RequestParam Optional<Integer> height,
//            @RequestParam Optional<Double> ratio,
//            @RequestParam Long ownerId
//    );
//
//}
//
//
