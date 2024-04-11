package com.st.eighteen_be.chat.service.impl;

import com.st.eighteen_be.chat.model.collection.ChatMessageCollection;
import com.st.eighteen_be.chat.model.collection.ChatroomInfoCollection;
import com.st.eighteen_be.chat.model.dto.request.ChatMessageRequestDTO;
import com.st.eighteen_be.chat.model.vo.ChatroomType;
import com.st.eighteen_be.chat.repository.ChatMessageCollectionRepository;
import com.st.eighteen_be.chat.repository.ChatroomInfoCollectionRepository;
import com.st.eighteen_be.common.exception.ErrorCode;
import com.st.eighteen_be.common.exception.sub_exceptions.data_exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatMessageService {
    
    private final ChatMessageCollectionRepository chatMessageCollectionRepository;
    private final ChatroomInfoCollectionRepository chatroomInfoCollectionRepository;
    
    @Transactional(readOnly = false)
    public void processMessage(ChatMessageRequestDTO messageDto) {
        ChatMessageCollection chatMessage = messageDto.toCollection();
        
        chatroomInfoCollectionRepository.findByRoomId(messageDto.roomId())
                .ifPresentOrElse(
                        chatroomInfo -> addMessage(chatMessage),
                        () -> throw new NotFoundException(ErrorCode.NOT_FOUND_CHATROOM_TYPE);
                );
    }
    
    private void createNewChatroom(String roomId) {
        chatroomInfoCollectionRepository.save(ChatroomInfoCollection.of(roomId, ChatroomType.PRIVATE));
    }
    
    private void addMessage(ChatMessageCollection chatMessage) {
        chatMessageCollectionRepository.save(chatMessage);
    }
}