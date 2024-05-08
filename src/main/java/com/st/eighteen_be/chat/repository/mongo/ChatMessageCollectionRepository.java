package com.st.eighteen_be.chat.repository.mongo;

import com.st.eighteen_be.chat.model.collection.ChatMessageCollection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;
import java.util.List;

public interface ChatMessageCollectionRepository extends MongoRepository<ChatMessageCollection, String> {
    List<ChatMessageCollection> findBySenderNoAndReceiverNoAndCreatedAtBefore(Long senderNo, Long receiverNo, @NonNull LocalDateTime createdAt, Pageable pageable);
}