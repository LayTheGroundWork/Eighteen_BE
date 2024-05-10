package com.st.eighteen_be.chat.service.facade;

import com.st.eighteen_be.chat.model.collection.ChatroomInfoCollection;
import com.st.eighteen_be.chat.model.dto.request.EnterChatRoomRequestDTO;
import com.st.eighteen_be.chat.model.dto.response.ChatMessageResponseDTO;
import com.st.eighteen_be.chat.service.ChatMessageService;
import com.st.eighteen_be.chat.service.ChatroomService;
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

    @Transactional(readOnly = false)
    public List<ChatMessageResponseDTO> getOrCreateChatroom(EnterChatRoomRequestDTO enterChatRoomRequestDTO) {
        log.info("getOrCreateChatroom.senderNo() = {}", enterChatRoomRequestDTO.senderNo());
        log.info("getOrCreateChatroom.receiverNo() = {}", enterChatRoomRequestDTO.receiverNo());

        ChatroomInfoCollection chatroomInfoCollection = chatroomService.getChatroom(enterChatRoomRequestDTO.senderNo(), enterChatRoomRequestDTO.receiverNo())
                .orElseGet(() -> chatroomService.createChatroom(enterChatRoomRequestDTO.senderNo(), enterChatRoomRequestDTO.receiverNo()));

        return chatMessageService.findMessagesBeforeTimeInRoom(chatroomInfoCollection.getSenderNo(), chatroomInfoCollection.getReceiverNo(), enterChatRoomRequestDTO.requestTime());
    }
}