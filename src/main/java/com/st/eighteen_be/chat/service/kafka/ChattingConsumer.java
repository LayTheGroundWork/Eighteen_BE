package com.st.eighteen_be.chat.service.kafka;

import com.st.eighteen_be.chat.constant.KafkaConst;
import com.st.eighteen_be.chat.model.collection.ChatMessageCollection;
import com.st.eighteen_be.chat.model.collection.ChatroomInfoCollection;
import com.st.eighteen_be.chat.model.dto.request.ChatMessageRequestDTO;
import com.st.eighteen_be.chat.model.vo.ChatroomType;
import com.st.eighteen_be.chat.repository.ChatMessageCollectionRepository;
import com.st.eighteen_be.chat.repository.ChatroomInfoCollectionRepository;
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
    private final ChatroomInfoCollectionRepository chatroomInfoCollectionRepository;
    private final ChatMessageCollectionRepository chatMessageCollectionRepository;
    
    @Transactional(readOnly = false)
    @KafkaListener(topics = KafkaConst.CHAT_TOPIC, groupId = KafkaConst.CHAT_CONSUMER_GROUP_ID)
    public void listen(ChatMessageRequestDTO messageDto) {
        log.info("ChattingConsumer.listen : roomId={}, sender={}, receiver={}, message={}: ", messageDto.roomId(), messageDto.sender(), messageDto.receiver(), messageDto.message());
        
        messagingTemplate.convertAndSendToUser(messageDto.receiver(), "/sub/chat/room/" + messageDto.roomId(), messageDto);
        
        ChatMessageCollection chatMessage = messageDto.toCollection();
        
        chatMessageCollectionRepository.save(chatMessage);
        
        ChatroomInfoCollection chatroomInfo = chatroomInfoCollectionRepository.findByRoomId(messageDto.roomId())
                .orElseGet(() -> ChatroomInfoCollection.of(messageDto.roomId(), messageDto.sender(), ChatroomType.PRIVATE));
        
        // 채팅방에 새 메시지 추가
        chatroomInfo.addChatMessage(chatMessage);
        
        // 변경된 채팅방 정보 저장
        chatroomInfoCollectionRepository.save(chatroomInfo);
    }
}