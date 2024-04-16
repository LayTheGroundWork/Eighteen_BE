package com.st.eighteen_be.chat.service.impl;

import com.st.eighteen_be.chat.model.collection.ChatMessageCollection;
import com.st.eighteen_be.chat.model.collection.ChatroomInfoCollection;
import com.st.eighteen_be.chat.model.dto.request.ChatMessageRequestDTO;
import com.st.eighteen_be.chat.model.vo.ChatroomType;
import com.st.eighteen_be.chat.repository.ChatMessageCollectionRepository;
import com.st.eighteen_be.chat.repository.ChatroomInfoCollectionRepository;
import com.st.eighteen_be.common.annotation.ServiceWithMongoDBTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * packageName    : com.st.eighteen_be.chat.service.impl
 * fileName       : ChatMessageServiceTest
 * author         : ipeac
 * date           : 24. 4. 9.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 4. 9.        ipeac       최초 생성
 */
@ServiceWithMongoDBTest
@ExtendWith(MockitoExtension.class)
public class ChatMessageServiceTest {
    
    @Autowired
    private MongoTemplate mongoTemplate;
    
    private ChatMessageService chatMessageService;
    
    @Autowired
    private ChatMessageCollectionRepository chatMessageCollectionRepository;
    @Autowired
    private ChatroomInfoCollectionRepository chatroomInfoCollectionRepository;
    
    private ChatMessageRequestDTO messageDto;
    
    private ChatroomInfoCollection chatroomInfoCollection;
    
    @BeforeEach
    void setUp() {
        // Given
        chatMessageService = new ChatMessageService(chatMessageCollectionRepository, chatroomInfoCollectionRepository);
        
        chatroomInfoCollection = ChatroomInfoCollection.builder()
                .roomId("1")
                .chatroomType(ChatroomType.PRIVATE)
                .build();
        
        messageDto = ChatMessageRequestDTO.builder()
                .roomId("1")
                .sender("sender")
                .receiver("receiver")
                .message("message")
                .build();
    }
    
    @Test
    @DisplayName("processMessage -  채팅방이 별도로 존재하지 않는 경우 채팅방을 생성하고 메시지를 전송함을 테스트")
    void When_processMessage_IfChatroomNotExist_Expect_CreateChatroomAndSendMessage() {
        // When
        chatMessageService.processMessage(messageDto);
        
        // Then
        ChatroomInfoCollection foundChatroom = chatroomInfoCollectionRepository.findByRoomId("1").get();
        ChatMessageCollection foundChatMessage = chatMessageCollectionRepository.findAll().get(0);
        
        assertThat(foundChatroom).isNotNull();
        assertThat(foundChatroom.getChatroomType()).isEqualTo(ChatroomType.PRIVATE);
        
        assertThat(foundChatMessage).isNotNull();
        assertThat(foundChatMessage.getRoomId()).isEqualTo("1");
        assertThat(foundChatMessage.getSender()).isEqualTo("sender");
        assertThat(foundChatMessage.getMessage()).isEqualTo("message");
        assertThat(foundChatMessage.getReceiver()).isEqualTo("receiver");
    }
    
    @Test
    @DisplayName("processMessage - 채팅방이 존재한 상태에서 메시지를 전송함을 테스트")
    void When_processMessage_IfChatroomExist_Expect_SendMessage() {
        // Given
        mongoTemplate.save(chatroomInfoCollection);
        
        // When
        chatMessageService.processMessage(messageDto);
        
        // Then
        ChatroomInfoCollection foundChatroom = chatroomInfoCollectionRepository.findByRoomId("1").get();
        ChatMessageCollection foundChatMessage = chatMessageCollectionRepository.findAll().get(0);
        
        assertThat(foundChatroom).isNotNull();
        assertThat(foundChatroom.getChatroomType()).isEqualTo(ChatroomType.PRIVATE);
        
        assertThat(foundChatMessage).isNotNull();
        assertThat(foundChatMessage.getRoomId()).isEqualTo("1");
        assertThat(foundChatMessage.getSender()).isEqualTo("sender");
        assertThat(foundChatMessage.getMessage()).isEqualTo("message");
        assertThat(foundChatMessage.getReceiver()).isEqualTo("receiver");
    }
    
    @AfterEach
    void tearDown() {
        for (String collectionName : mongoTemplate.getCollectionNames()) {
            mongoTemplate.dropCollection(collectionName);
        }
    }
}