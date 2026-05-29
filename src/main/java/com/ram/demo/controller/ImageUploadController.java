package com.ram.demo.controller;

package com.ram.demo.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/upload")
public class ImageUploadController {

    @Value("${app.upload.dir:uploads/products}")
    private String uploadDir;

    @Value("${app.base-url:http://localhost:9092}")
    private String baseUrl;

    @PostMapping("/image")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> uploadImage(
            @RequestParam("file") MultipartFile file) throws IOException {

        // validate file type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Only image files are allowed"));
        }

        // validate extension
        String originalName = file.getOriginalFilename();
        if (originalName == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid file name"));
        }
        String ext = originalName.substring(originalName.lastIndexOf('.') + 1).toLowerCase();
        if (!ext.equals("jpg") && !ext.equals("jpeg")
                && !ext.equals("png") && !ext.equals("webp")) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Only jpg, jpeg, png, webp allowed"));
        }

        // create upload directory if not exists
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // save file with unique name
        String fileName = UUID.randomUUID() + "." + ext;
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // return accessible URL
        String url = baseUrl + "/api/upload/files/" + fileName;
        return ResponseEntity.ok(Map.of("url", url, "fileName", fileName));
    }
}