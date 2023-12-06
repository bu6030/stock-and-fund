package com.buxuesong.account.apis.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
public class FileUploadController {

    // Specify the directory for file uploads
    private static final String UPLOAD_DIR = "/qr_code";

    @PostMapping("/upload")
    public ResponseEntity<String> handleFileUpload(@RequestPart("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please select a file to upload.");
        }

        try {
            String fileName = "WechatIMG274.jpeg";
            Path uploadPath = Paths.get(UPLOAD_DIR);
            Path filePath = uploadPath.resolve(fileName);
            // Save the file to the specified directory
            file.transferTo(filePath.toFile());

            return ResponseEntity.ok("File uploaded successfully: " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Failed to upload file.");
        }
    }
}
