package com.maitriconnect.chat_service.controller;

import com.maitriconnect.chat_service.dto.FileUploadResponse;
import com.maitriconnect.chat_service.model.FileMetadata;
import com.maitriconnect.chat_service.service.FileStorageService;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/api/chat/files")
// @CrossOrigin removed - API Gateway handles CORS
public class FileRestController {

    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") String userId,
            @RequestParam("chatRoomId") String chatRoomId) {
        
        try {
            FileMetadata metadata = fileStorageService.uploadFile(file, userId, chatRoomId);
            
            FileUploadResponse response = new FileUploadResponse(
                metadata.getId(),
                metadata.getOriginalFileName(),
                metadata.getFileType(),
                metadata.getFileSize(),
                metadata.getUploadedBy(),
                metadata.getUploadedAt().toString(),
                "/api/chat/files/download/" + metadata.getId()
            );
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to upload file: " + e.getMessage());
        }
    }

    @GetMapping("/download/{fileId}")
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable String fileId) {
        try {
            FileMetadata metadata = fileStorageService.getFileMetadata(fileId);
            InputStream fileStream = fileStorageService.getFileStream(fileId);
            
            InputStreamResource resource = new InputStreamResource(fileStream);
            
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + metadata.getOriginalFileName() + "\"")
                .contentType(MediaType.parseMediaType(metadata.getFileType()))
                .contentLength(metadata.getFileSize())
                .body(resource);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/metadata/{fileId}")
    public ResponseEntity<?> getFileMetadata(@PathVariable String fileId) {
        try {
            FileMetadata metadata = fileStorageService.getFileMetadata(fileId);
            return ResponseEntity.ok(metadata);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{fileId}")
    public ResponseEntity<?> deleteFile(@PathVariable String fileId) {
        try {
            fileStorageService.deleteFile(fileId);
            return ResponseEntity.ok().body("File deleted successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
