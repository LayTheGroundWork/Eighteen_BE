package com.st.eighteen_be.chat.repository;

import com.st.eighteen_be.chat.model.collection.ChatMessageCollection;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatMessageCollectionRepository extends MongoRepository<ChatMessageCollection, Long> {
}