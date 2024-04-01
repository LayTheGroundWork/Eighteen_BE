package com.st.eighteen_be.chat.api;

import com.st.eighteen_be.chat.constant.KafkaConst;
import com.st.eighteen_be.chat.model.dto.request.ChatMessageRequestDTO;
import com.st.eighteen_be.chat.service.kafka.ChattingProducer;
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
    private final ChattingProducer chattingProducer;
    
    @MessageMapping("/chat/{chatroomId}/message") // /pub/chat/message
    public void sendMessage(@DestinationVariable String chatroomId, ChatMessageRequestDTO chatMessage) {
        log.info("chatroomId : {}", chatroomId);
        log.info("chatMessage.message() = " + chatMessage.message());
        
        chattingProducer.send(KafkaConst.CHAT_TOPIC + chatroomId, chatMessage);
    }
}