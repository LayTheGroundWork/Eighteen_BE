package com.st.eighteen_be.chat.service;

import com.st.eighteen_be.chat.model.collection.ChatroomInfoCollection;
import com.st.eighteen_be.chat.model.dto.request.FindChatRoomRequestDTO;
import com.st.eighteen_be.chat.model.dto.response.ChatroomWithLastestMessageDTO;
import com.st.eighteen_be.chat.model.vo.ChatroomType;
import com.st.eighteen_be.chat.repository.mongo.ChatroomInfoCollectionRepository;
import com.st.eighteen_be.chat.service.helper.ChatUserHelper;
import com.st.eighteen_be.chat.service.redis.RedisMessageService;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ChatroomService {
    
    private final ChatroomInfoCollectionRepository chatroomInfoCollectionRepository;
    private final RedisMessageService redisMessageService;
    
    @Transactional(readOnly = false)
    public ChatroomInfoCollection createChatroom(final @NonNull String senderId, final @NonNull String receiverId) {
        log.info("========== createChatroom ========== senderId : {}, receiverId : {}", senderId, receiverId);
        
        ChatUserHelper.validNotSameUser(senderId, receiverId);
        
        ChatroomInfoCollection newChatroom = ChatroomInfoCollection.of(senderId, receiverId, ChatroomType.PRIVATE);
        return chatroomInfoCollectionRepository.save(newChatroom);
    }
    
    public Optional<ChatroomInfoCollection> getChatroom(@NonNull final String chatroomInfoId) {
        log.info("========== getChatroom ========== chatroomInfoId : {}", chatroomInfoId);
        
        return chatroomInfoCollectionRepository.findById(chatroomInfoId);
    }
    
    public List<ChatroomWithLastestMessageDTO> findAllMyChatrooms(@Valid FindChatRoomRequestDTO requestDTO) {
        log.info("========== findAllMyChatrooms ========== senderNo : {}", requestDTO.senderId());
        
        List<ChatroomWithLastestMessageDTO> allChatroomBySenderNo = chatroomInfoCollectionRepository.findAllChatroomBySenderNo(requestDTO.senderId());
        
        for (ChatroomWithLastestMessageDTO chatroomWithLastestMessageDTO : allChatroomBySenderNo) {
            long unreadMessageCount = redisMessageService.getUnreadMessageCount(chatroomWithLastestMessageDTO.getSenderId(), chatroomWithLastestMessageDTO.getReceiverId());
            chatroomWithLastestMessageDTO.setUnreadMessageCount(unreadMessageCount);
        }
        
        return allChatroomBySenderNo;
    }
    
    public void quitChatroom(String chatroomId, String quitUserId) {
        log.info("========== quitChatroom ========== chatroomId : {}, userNo : {}", chatroomId, quitUserId);
        
        chatroomInfoCollectionRepository.findById(chatroomId)
                .ifPresent(chatroomInfoCollection -> {
                    if (chatroomInfoCollection.isUserInChatroom(quitUserId)) {
                        chatroomInfoCollection.addLeftUser(quitUserId);
                        chatroomInfoCollectionRepository.save(chatroomInfoCollection);
                    }
                });
    }
}
