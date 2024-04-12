package com.st.eighteen_be.chat.service.impl;

import com.st.eighteen_be.chat.model.collection.ChatroomInfoCollection;
import com.st.eighteen_be.chat.model.vo.ChatroomType;
import com.st.eighteen_be.chat.repository.ChatroomInfoCollectionRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ChatroomService {
    
    private final ChatroomInfoCollectionRepository chatroomInfoCollectionRepository;
    
    @Transactional(readOnly = false)
    public ChatroomInfoCollection createChatroom(@NonNull final Long postNo, @NonNull final Long memberNo) {
        log.info("========== createChatroom ========== postNo : {}, memberNo : {}", postNo, memberNo);
        
        ChatroomInfoCollection newChatroom = ChatroomInfoCollection.of(postNo, memberNo, ChatroomType.PRIVATE);
        return chatroomInfoCollectionRepository.save(newChatroom);
    }
    
    public Optional<ChatroomInfoCollection> getChatroom(@NonNull final Long postNo, @NonNull final Long memberNo) {
        log.info("========== getChatroom ========== postNo : {}, memberNo : {}", postNo, memberNo);
        
        return chatroomInfoCollectionRepository.findByPostNoAndMemberNo(postNo, memberNo);
    }
}