package com.st.eighteen_be.chat.service.kafka;

import com.st.eighteen_be.chat.model.collection.ChatroomInfoCollection;
import com.st.eighteen_be.chat.model.dto.request.ChatMessageRequestDTO;
import com.st.eighteen_be.chat.service.impl.ChatroomService;
import com.st.eighteen_be.common.exception.ErrorCode;
import com.st.eighteen_be.common.exception.sub_exceptions.data_exceptions.NotFoundException;
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
    private final ChatroomService chatroomService;
    
    public void send(String topic, ChatMessageRequestDTO messageDto) {
        log.info("ChattingProducer.sendTopic : {}", topic);
        log.info("ChattingProducer.messageContent : senderNo={}, receiverNo={}, message={}: ", messageDto.getSenderNo(), messageDto.getReceiverNo(), messageDto.getMessage());
        
        setChatroomInfo(messageDto);
        
        kafkaTemplate.send(topic, messageDto);
    }
    
    private void setChatroomInfo(ChatMessageRequestDTO messageDto) {
        ChatroomInfoCollection foundedChatroom = chatroomService.getChatroom(messageDto.getSenderNo(), messageDto.getReceiverNo())
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_CHATROOM));
        
        messageDto.setChatroomInfoId(foundedChatroom.get_id());
    }
}