package com.st.eighteen_be.chat.api;

import com.st.eighteen_be.chat.constant.KafkaConst;
import com.st.eighteen_be.chat.model.dto.request.ChatMessageRequestDTO;
import com.st.eighteen_be.chat.service.kafka.ChattingProducer;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
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
    private final ChattingProducer chattingProducer;
    
    @MessageMapping("/chat/{postNo}/{memberNo}/enter") // /pub/chat/{postNo}/{memberNo}/create
    public void enterChatroom(@DestinationVariable(value = "postNo") Long postNo, @DestinationVariable(value = "memberNo") Long memberNo) {
        log.info("postNo : {}", postNo);
        log.info("memberNo : {}", memberNo);
        
        
    }
    
    @MessageMapping("/chat/{chatroomId}/message") // /pub/chat/{chatroomId}/message
    public void sendMessage(@DestinationVariable(value = "chatroomId") Long chatroomId, @Valid ChatMessageRequestDTO chatMessage) {
        log.info("chatroomId : {}", chatroomId);
        log.info("chatMessage.message() = {}", chatMessage.message());
        
        chattingProducer.send(KafkaConst.CHAT_TOPIC, chatMessage);
    }
}