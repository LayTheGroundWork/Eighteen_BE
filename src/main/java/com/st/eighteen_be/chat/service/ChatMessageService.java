package com.st.eighteen_be.chat.service;

import com.st.eighteen_be.chat.model.collection.ChatMessageCollection;
import com.st.eighteen_be.chat.model.dto.request.ChatMessageRequestDTO;
import com.st.eighteen_be.chat.model.dto.response.ChatMessageResponseDTO;
import com.st.eighteen_be.chat.repository.mongo.ChatMessageCollectionRepository;
import com.st.eighteen_be.chat.repository.mongo.ChatroomInfoCollectionRepository;
import com.st.eighteen_be.chat.service.redis.RedisMessageService;
import com.st.eighteen_be.common.exception.ErrorCode;
import com.st.eighteen_be.common.exception.sub_exceptions.data_exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ChatMessageService {

    private final ChatMessageCollectionRepository chatMessageCollectionRepository;
    private final ChatroomInfoCollectionRepository chatroomInfoCollectionRepository;
    private final RedisMessageService redisMessageService;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional(readOnly = false)
    public void processMessage(ChatMessageRequestDTO messageDto) {
        log.info("========== processMessage ========== senderId : {}, receiverId : {}", messageDto.getSenderId(), messageDto.getReceiverId());
        ChatMessageCollection chatMessage = messageDto.toCollection();

        chatroomInfoCollectionRepository.findById(chatMessage.getChatroomInfoId().toString())
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

    public List<ChatMessageResponseDTO> findMessagesBeforeTimeInRoom(String senderId, String receiverId, LocalDateTime lastMessageTime) {
        log.info("========== findMessagesBeforeTimeInRoom ========== senderId : {}, receiverId : {}, lastMessageTime : {}", senderId, receiverId, lastMessageTime);

        Pageable pageable = PageRequest.of(0, 20, Sort.by("createdAt").descending());

        List<ChatMessageCollection> foundChatMessages = chatMessageCollectionRepository.findBySenderIdAndReceiverIdAndCreatedAtBefore(senderId, receiverId, lastMessageTime, pageable);

        return foundChatMessages.stream()
                .map(ChatMessageCollection::toResponseDTO)
                .toList();
    }

    private void addMessage(ChatMessageCollection chatMessage) {
        log.info("========== addMessage ==========");

        chatMessageCollectionRepository.save(chatMessage);

        redisMessageService.incrementUnreadMessageCount(chatMessage.getSenderId(), chatMessage.getReceiverId());
    }
    
public void send(ChatMessageRequestDTO messageDto, String chatroomId) {
        messageDto.setChatroomInfoId(chatroomId);
        this.processMessage(messageDto);
        messagingTemplate.convertAndSend(MessageFormat.format("/sub/v1/api/chat/{0}/message", messageDto.getChatroomInfoId()), messageDto);
    }
}
