package com.st.eighteen_be.chat.service.kafka;

import com.st.eighteen_be.chat.model.dto.request.ChatMessageRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

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
@RequiredArgsConstructor
@Slf4j
public class ChattingProducer {
    
    private final KafkaTemplate<String, ChatMessageRequestDTO> kafkaTemplate;
    
    public void send(String topic, ChatMessageRequestDTO messageDto) {
        log.info("ChattingProducer.sendTopic : " + topic);
        log.info("ChattingProducer.messageContent : roomId={}, sender={}, receiver={}, message={}: ", messageDto.roomId(), messageDto.sender(), messageDto.receiver(), messageDto.message());
        kafkaTemplate.send(topic, messageDto);
    }
}