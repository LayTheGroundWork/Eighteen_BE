package com.st.eighteen_be.chat.service.facade;

import com.st.eighteen_be.chat.model.collection.ChatroomInfoCollection;
import com.st.eighteen_be.chat.model.dto.request.EnterChatRoomRequestDTO;
import com.st.eighteen_be.chat.model.dto.response.ChatMessageResponseDTO;
import com.st.eighteen_be.chat.model.redishash.UnreadMessageCount;
import com.st.eighteen_be.chat.service.ChatMessageService;
import com.st.eighteen_be.chat.service.ChatroomService;
import com.st.eighteen_be.chat.service.redis.RedisMessageService;
import com.st.eighteen_be.common.exception.ErrorCode;
import com.st.eighteen_be.common.exception.sub_exceptions.data_exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * packageName    : com.st.eighteen_be.chat.service.facade
 * fileName       : ChatroomFacade
 * author         : ipeac
 * date           : 24. 4. 12.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 4. 12.        ipeac       최초 생성
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ChatroomFacade {
    private final ChatroomService chatroomService;
    private final ChatMessageService chatMessageService;
    private final RedisMessageService redisMessageService;
    
    @Transactional(readOnly = false)
    public List<ChatMessageResponseDTO> getChatroom(EnterChatRoomRequestDTO enterChatRoomRequestDTO) {
        log.info("========== getOrCreateChatroom ========== chatroomInfoId : {}, requestTime : {}", enterChatRoomRequestDTO.chatroomInfoId(), enterChatRoomRequestDTO.requestTime());
        
        ChatroomInfoCollection chatroomInfoCollection = chatroomService.getChatroom(enterChatRoomRequestDTO.chatroomInfoId())
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_CHATROOM));
        
        redisMessageService.resetUnreadMessageCount(UnreadMessageCount.forChatroomEntry(chatroomInfoCollection.getSenderId(), chatroomInfoCollection.getReceiverId(), 0L));
        
        return chatMessageService.findMessagesBeforeTimeInRoom(chatroomInfoCollection.getSenderId(), chatroomInfoCollection.getReceiverId(), enterChatRoomRequestDTO.requestTime());
    }
    
    @Transactional(readOnly = false)
    public void quitChatroom(String chatroomId, String quitUserId) {
        log.info("========== quitChatroom ========== chatroomId : {}, userNo : {}", chatroomId, quitUserId);
        
        ChatroomInfoCollection chatroomInfoCollection = chatroomService.getChatroom(chatroomId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_CHATROOM));
        
        chatroomService.quitChatroom(chatroomId, quitUserId);
    }
}
