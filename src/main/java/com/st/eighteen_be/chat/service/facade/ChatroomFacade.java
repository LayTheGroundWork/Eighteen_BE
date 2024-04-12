package com.st.eighteen_be.chat.service.facade;

import com.st.eighteen_be.chat.model.collection.ChatroomInfoCollection;
import com.st.eighteen_be.chat.model.dto.request.EnterChatRoomRequestDTO;
import com.st.eighteen_be.chat.model.dto.response.ChatMessageResponseDTO;
import com.st.eighteen_be.chat.service.impl.ChatMessageService;
import com.st.eighteen_be.chat.service.impl.ChatroomService;
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
        ChatroomInfoCollection chatroomInfoCollection = chatroomService.getChatroom(enterChatRoomRequestDTO.postNo(), enterChatRoomRequestDTO.memberNo())
                .orElseGet(() -> chatroomService.createChatroom(enterChatRoomRequestDTO.postNo(), enterChatRoomRequestDTO.memberNo()));
        
        return chatMessageService.findMessagesBeforeTimeInRoom(chatroomInfoCollection.getRoomId(), enterChatRoomRequestDTO.requestTime());
    }
}