package com.st.eighteen_be.chat.service.kafka;

import com.st.eighteen_be.chat.constant.KafkaConst;
import com.st.eighteen_be.chat.model.dto.request.ChatMessageRequestDTO;
import com.st.eighteen_be.chat.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * packageName    : com.st.eighteen_be.chat.service.kafka
 * fileName       : ChattingProducer
 * author         : ipeac
 * date           : 2024-04-01
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-04-01        ipeac       최초 생성
 */
@Component
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ChattingConsumer {
    
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageService chatMessageService;
    
    @Transactional(readOnly = false)
    @KafkaListener(topics = KafkaConst.CHAT_TOPIC, groupId = KafkaConst.CHAT_CONSUMER_GROUP_ID)
    public void listen(ChatMessageRequestDTO messageDto) {
        log.info("ChattingConsumer.listen : roomId={}, sender={}, receiver={}, message={}: ", messageDto.roomId(), messageDto.sender(), messageDto.receiver(), messageDto.message());
        
        messagingTemplate.convertAndSend("/sub/chat/" + messageDto.roomId(), messageDto);
        
        chatMessageService.processMessage(messageDto);
    }
}