package com.st.eighteen_be.chat.repository;

import com.st.eighteen_be.chat.model.collection.ChatMessageCollection;
import com.st.eighteen_be.chat.model.dto.response.ChatMessageResponseDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ChatMessageCollectionRepository extends MongoRepository<ChatMessageCollection, Long> {
    List<ChatMessageResponseDTO> findByRoomIdAndCreatedAtBefore(String roomId, LocalDateTime lastMessageTime, Pageable pageable);
}