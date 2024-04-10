package com.st.eighteen_be.chat.service.impl;

import com.st.eighteen_be.chat.model.collection.ChatMessageCollection;
import com.st.eighteen_be.chat.model.collection.ChatroomInfoCollection;
import com.st.eighteen_be.chat.model.dto.request.ChatMessageRequestDTO;
import com.st.eighteen_be.chat.model.vo.ChatroomType;
import com.st.eighteen_be.chat.repository.ChatMessageCollectionRepository;
import com.st.eighteen_be.chat.repository.ChatroomInfoCollectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatMessageService {
    
    private final ChatMessageCollectionRepository chatMessageCollectionRepository;
    private final ChatroomInfoCollectionRepository chatroomInfoCollectionRepository;
    
    @Transactional(readOnly = false)
    public void processMessage(ChatMessageRequestDTO messageDto) {
        ChatMessageCollection chatMessage = messageDto.toCollection();
        
        Optional<ChatroomInfoCollection> foundRoom = chatroomInfoCollectionRepository.findByRoomId(messageDto.roomId());
        
        if (foundRoom.isEmpty()) {
            createNewChatroom(messageDto.roomId());
        }
        
        addMessage(chatMessage);
    }
    
    private void createNewChatroom(String roomId) {
        chatroomInfoCollectionRepository.save(ChatroomInfoCollection.of(roomId, ChatroomType.PRIVATE));
    }
    
    private void addMessage(ChatMessageCollection chatMessage) {
        chatMessageCollectionRepository.save(chatMessage);
    }
}