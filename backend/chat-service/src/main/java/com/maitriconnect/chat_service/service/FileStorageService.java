package com.maitriconnect.chat_service.service;

import com.maitriconnect.chat_service.model.FileMetadata;
import com.maitriconnect.chat_service.repository.FileMetadataRepository;
import com.mongodb.client.gridfs.model.GridFSFile;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class FileStorageService {

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private FileMetadataRepository fileMetadataRepository;

    private static final long MAX_FILE_SIZE = 25 * 1024 * 1024; // 25MB
    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList("image/jpeg", "image/png", "image/jpg", "image/gif", "image/webp");
    private static final List<String> ALLOWED_DOCUMENT_TYPES = Arrays.asList("application/pdf", "application/msword", 
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        "text/plain");

    public FileMetadata uploadFile(MultipartFile file, String userId, String chatRoomId) throws IOException {
        // Validate file size
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds maximum limit of 25MB");
        }

        // Validate file type
        String contentType = file.getContentType();
        if (!isAllowedFileType(contentType)) {
            throw new IllegalArgumentException("File type not allowed: " + contentType);
        }

        String originalFileName = file.getOriginalFilename();
        String uniqueFileName = UUID.randomUUID().toString() + "_" + originalFileName;

        InputStream inputStream;
        boolean isCompressed = false;
        String gridFsFileId;

        // Compress images
        if (ALLOWED_IMAGE_TYPES.contains(contentType)) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            try {
                Thumbnails.of(file.getInputStream())
                    .size(1920, 1920) // Max dimensions
                    .outputQuality(0.85) // 85% quality
                    .toOutputStream(outputStream);
                
                inputStream = new ByteArrayInputStream(outputStream.toByteArray());
                isCompressed = true;
            } catch (Exception e) {
                // If compression fails, use original
                inputStream = file.getInputStream();
            }
        } else {
            inputStream = file.getInputStream();
        }

        // Store in GridFS
        gridFsFileId = gridFsTemplate.store(inputStream, uniqueFileName, contentType).toString();

        // Create metadata
        FileMetadata metadata = new FileMetadata(
            uniqueFileName,
            originalFileName,
            contentType,
            file.getSize(),
            gridFsFileId,
            userId,
            chatRoomId
        );
        metadata.setIsCompressed(isCompressed);

        // Create thumbnail for images
        if (ALLOWED_IMAGE_TYPES.contains(contentType)) {
            try {
                ByteArrayOutputStream thumbnailStream = new ByteArrayOutputStream();
                Thumbnails.of(file.getInputStream())
                    .size(200, 200)
                    .outputQuality(0.7)
                    .toOutputStream(thumbnailStream);
                
                String thumbnailId = gridFsTemplate.store(
                    new ByteArrayInputStream(thumbnailStream.toByteArray()),
                    "thumb_" + uniqueFileName,
                    contentType
                ).toString();
                
                metadata.setThumbnailId(thumbnailId);
            } catch (Exception e) {
                // Thumbnail creation is optional
                System.err.println("Failed to create thumbnail: " + e.getMessage());
            }
        }

        return fileMetadataRepository.save(metadata);
    }

    public GridFSFile getFile(String fileId) {
        FileMetadata metadata = fileMetadataRepository.findById(fileId)
            .orElseThrow(() -> new IllegalArgumentException("File not found: " + fileId));
        
        return gridFsTemplate.findOne(new Query(Criteria.where("_id").is(metadata.getGridFsFileId())));
    }

    public InputStream getFileStream(String fileId) throws IOException {
        FileMetadata metadata = fileMetadataRepository.findById(fileId)
            .orElseThrow(() -> new IllegalArgumentException("File not found: " + fileId));
        
        GridFSFile gridFSFile = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(metadata.getGridFsFileId())));
        if (gridFSFile == null) {
            throw new IllegalArgumentException("File not found in GridFS");
        }
        
        return gridFsTemplate.getResource(gridFSFile).getInputStream();
    }

    public FileMetadata getFileMetadata(String fileId) {
        return fileMetadataRepository.findById(fileId)
            .orElseThrow(() -> new IllegalArgumentException("File metadata not found: " + fileId));
    }

    public void deleteFile(String fileId) {
        FileMetadata metadata = fileMetadataRepository.findById(fileId)
            .orElseThrow(() -> new IllegalArgumentException("File not found: " + fileId));
        
        // Delete from GridFS
        gridFsTemplate.delete(new Query(Criteria.where("_id").is(metadata.getGridFsFileId())));
        
        // Delete thumbnail if exists
        if (metadata.getThumbnailId() != null) {
            gridFsTemplate.delete(new Query(Criteria.where("_id").is(metadata.getThumbnailId())));
        }
        
        // Delete metadata
        fileMetadataRepository.delete(metadata);
    }

    private boolean isAllowedFileType(String contentType) {
        return ALLOWED_IMAGE_TYPES.contains(contentType) || ALLOWED_DOCUMENT_TYPES.contains(contentType);
    }
}
