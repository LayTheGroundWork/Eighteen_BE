package com.st.eighteen_be.chat.service.impl;

import com.st.eighteen_be.chat.model.collection.ChatMessageCollection;
import com.st.eighteen_be.chat.model.dto.request.ChatMessageRequestDTO;
import com.st.eighteen_be.chat.model.dto.response.ChatMessageResponseDTO;
import com.st.eighteen_be.chat.repository.ChatMessageCollectionRepository;
import com.st.eighteen_be.chat.repository.ChatroomInfoCollectionRepository;
import com.st.eighteen_be.common.exception.ErrorCode;
import com.st.eighteen_be.common.exception.sub_exceptions.data_exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ChatMessageService {
    
    private final ChatMessageCollectionRepository chatMessageCollectionRepository;
    private final ChatroomInfoCollectionRepository chatroomInfoCollectionRepository;
    
    @Transactional(readOnly = false)
    public void processMessage(ChatMessageRequestDTO messageDto) {
        log.info("========== processMessage ========== senderId : {} roomId : {}", messageDto.senderNo(), messageDto.roomId());
        
        ChatMessageCollection chatMessage = messageDto.toCollection();
        
        chatroomInfoCollectionRepository.findByRoomId(messageDto.roomId())
                .ifPresentOrElse(
                        chatroomInfo -> {
                            log.info("========== chatroom found ==========");
                            
                            addMessage(chatMessage);
                        },
                        () -> {
                            log.info("========== chatroom not found ==========");
                            
                            throw new NotFoundException(ErrorCode.NOT_FOUND_CHATROOM);
                        }
                );
    }
    
    public List<ChatMessageResponseDTO> findMessagesBeforeTimeInRoom(String roomId, LocalDateTime lastMessageTime) {
        log.info("========== getMessages  roomId : {} lastMessageTime : {} ==========", roomId, lastMessageTime);
        
        Pageable pageable = PageRequest.of(0, 20, Sort.by("createdAt").descending());
        
        return chatMessageCollectionRepository.findByRoomIdAndCreatedAtBefore(roomId, lastMessageTime, pageable);
    }
    
    private void addMessage(ChatMessageCollection chatMessage) {
        log.info("========== addMessage ==========");
        
        chatMessageCollectionRepository.save(chatMessage);
    }
}