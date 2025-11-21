package com.maitriconnect.chat_service.repository;

import com.maitriconnect.chat_service.model.FileMetadata;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileMetadataRepository extends MongoRepository<FileMetadata, String> {
    List<FileMetadata> findByMessageId(String messageId);
    List<FileMetadata> findByChatRoomId(String chatRoomId);
    List<FileMetadata> findByUploadedBy(String userId);
}
