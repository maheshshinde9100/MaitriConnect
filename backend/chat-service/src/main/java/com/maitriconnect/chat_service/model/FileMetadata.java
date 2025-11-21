package com.maitriconnect.chat_service.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "file_metadata")
public class FileMetadata {
    @Id
    private String id;
    private String fileName;
    private String originalFileName;
    private String fileType;
    private Long fileSize;
    private String gridFsFileId; // GridFS file ID
    private String uploadedBy;
    private LocalDateTime uploadedAt;
    private String messageId;
    private String chatRoomId;
    private Boolean isCompressed;
    private String thumbnailId; // For image thumbnails

    public FileMetadata() {
        this.uploadedAt = LocalDateTime.now();
        this.isCompressed = false;
    }

    public FileMetadata(String fileName, String originalFileName, String fileType, Long fileSize, 
                       String gridFsFileId, String uploadedBy, String chatRoomId) {
        this.fileName = fileName;
        this.originalFileName = originalFileName;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.gridFsFileId = gridFsFileId;
        this.uploadedBy = uploadedBy;
        this.chatRoomId = chatRoomId;
        this.uploadedAt = LocalDateTime.now();
        this.isCompressed = false;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getOriginalFileName() { return originalFileName; }
    public void setOriginalFileName(String originalFileName) { this.originalFileName = originalFileName; }

    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }

    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }

    public String getGridFsFileId() { return gridFsFileId; }
    public void setGridFsFileId(String gridFsFileId) { this.gridFsFileId = gridFsFileId; }

    public String getUploadedBy() { return uploadedBy; }
    public void setUploadedBy(String uploadedBy) { this.uploadedBy = uploadedBy; }

    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }

    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }

    public String getChatRoomId() { return chatRoomId; }
    public void setChatRoomId(String chatRoomId) { this.chatRoomId = chatRoomId; }

    public Boolean getIsCompressed() { return isCompressed; }
    public void setIsCompressed(Boolean isCompressed) { this.isCompressed = isCompressed; }

    public String getThumbnailId() { return thumbnailId; }
    public void setThumbnailId(String thumbnailId) { this.thumbnailId = thumbnailId; }
}
