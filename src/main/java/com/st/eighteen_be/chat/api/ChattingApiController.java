package com.st.eighteen_be.chat.api;

import com.st.eighteen_be.chat.model.entity.ChatMessageEntityRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

/**
 * packageName    : com.st.eighteen_be.chat.api
 * fileName       : ChattingApiController
 * author         : ipeac
 * date           : 2024-03-29
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-03-29        ipeac       최초 생성
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class ChattingApiController {
    private final SimpMessagingTemplate messagingTemplate;
    
    @MessageMapping("/chat/{chatroomId}/message") // /pub/chat/message
    public void sendMessage(@DestinationVariable String chatroomId, ChatMessageEntityRequestDTO chatMessage) {
        log.info("chatroomId : {}", chatroomId);
        log.info("sender : {}", chatMessage.sender());
        
        messagingTemplate.convertAndSendToUser(chatMessage.sender(), "/sub/chat/room/" + chatroomId, chatMessage);
    }
}