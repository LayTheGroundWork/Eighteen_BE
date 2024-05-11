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
    private final RedisMessageService redisMessageService;

    @Transactional(readOnly = false)
    public void processMessage(ChatMessageRequestDTO messageDto) {
        log.info("========== processMessage ========== senderNo : {}, receiverNo : {}", messageDto.getSenderNo(), messageDto.getReceiverNo());

        ChatMessageCollection chatMessage = messageDto.toCollection();

        chatroomInfoCollectionRepository.findBySenderNoAndReceiverNo(chatMessage.getSenderNo(), chatMessage.getReceiverNo())
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

    public List<ChatMessageResponseDTO> findMessagesBeforeTimeInRoom(Long senderNo, Long receiverNo, LocalDateTime lastMessageTime) {
        log.info("========== findMessagesBeforeTimeInRoom ========== senderNo : {}, receiverNo : {}, lastMessageTime : {}", senderNo, receiverNo, lastMessageTime);

        Pageable pageable = PageRequest.of(0, 20, Sort.by("createdAt").descending());

        List<ChatMessageCollection> foundChatMessages = chatMessageCollectionRepository.findBySenderNoAndReceiverNoAndCreatedAtBefore(senderNo, receiverNo, lastMessageTime, pageable);

        return foundChatMessages.stream()
                .map(ChatMessageCollection::toResponseDTO)
                .toList();
    }

    private void addMessage(ChatMessageCollection chatMessage) {
        log.info("========== addMessage ==========");

        chatMessageCollectionRepository.save(chatMessage);

        redisMessageService.incrementUnreadMessageCount(chatMessage.getSenderNo(), chatMessage.getReceiverNo());
    }
}