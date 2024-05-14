package com.st.eighteen_be.chat.service.facade;

import com.st.eighteen_be.chat.model.collection.ChatroomInfoCollection;
import com.st.eighteen_be.chat.model.dto.request.EnterChatRoomRequestDTO;
import com.st.eighteen_be.chat.model.dto.response.ChatMessageResponseDTO;
import com.st.eighteen_be.chat.model.redishash.UnreadMessageCount;
import com.st.eighteen_be.chat.service.ChatMessageService;
import com.st.eighteen_be.chat.service.ChatroomService;
import com.st.eighteen_be.chat.service.redis.RedisMessageService;
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
    public List<ChatMessageResponseDTO> getOrCreateChatroom(EnterChatRoomRequestDTO enterChatRoomRequestDTO) {
        log.info("getOrCreateChatroom.senderNo() = {}", enterChatRoomRequestDTO.senderNo());
        log.info("getOrCreateChatroom.receiverNo() = {}", enterChatRoomRequestDTO.receiverNo());

        //TODO 만약 발신자와 수신자의 위치가 변경된다면 채팅방이 아예 새로 생성되는 문제가 발생한다. 이게 올바르지는 않다는 것을 인지하고 있으나, 현재는 이렇게 구현되어 있다.
        ChatroomInfoCollection chatroomInfoCollection = chatroomService.getChatroom(enterChatRoomRequestDTO.senderNo(), enterChatRoomRequestDTO.receiverNo())
                .orElseGet(() -> chatroomService.createChatroom(enterChatRoomRequestDTO.senderNo(), enterChatRoomRequestDTO.receiverNo()));

        redisMessageService.resetUnreadMessageCount(UnreadMessageCount.forChatroomEntry(enterChatRoomRequestDTO.receiverNo(), enterChatRoomRequestDTO.senderNo()));

        return chatMessageService.findMessagesBeforeTimeInRoom(chatroomInfoCollection.getSenderNo(), chatroomInfoCollection.getReceiverNo(), enterChatRoomRequestDTO.requestTime());
    }
}