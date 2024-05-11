package com.st.eighteen_be.chat.service.impl;

import com.st.eighteen_be.chat.model.collection.ChatMessageCollection;
import com.st.eighteen_be.chat.model.collection.ChatroomInfoCollection;
import com.st.eighteen_be.chat.model.dto.request.ChatMessageRequestDTO;
import com.st.eighteen_be.chat.model.vo.ChatroomType;
import com.st.eighteen_be.chat.repository.mongo.ChatMessageCollectionRepository;
import com.st.eighteen_be.chat.repository.mongo.ChatroomInfoCollectionRepository;
import com.st.eighteen_be.chat.service.ChatMessageService;
import com.st.eighteen_be.chat.service.redis.RedisMessageService;
import com.st.eighteen_be.common.annotation.ServiceWithMongoDBTest;
import com.st.eighteen_be.common.exception.sub_exceptions.data_exceptions.NotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
@DisplayName("ChatMessageService 테스트")
@ServiceWithMongoDBTest
public class ChatMessageServiceTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    private ChatMessageService chatMessageService;

    private RedisMessageService redisMessageService;

    @Autowired
    private ChatMessageCollectionRepository chatMessageCollectionRepository;

    @Autowired
    private ChatroomInfoCollectionRepository chatroomInfoCollectionRepository;

    private ChatMessageRequestDTO messageDto;

    private ChatroomInfoCollection chatroomInfoCollection;

    @BeforeEach
    void setUp() {
        // Given
        chatMessageService = new ChatMessageService(chatMessageCollectionRepository, chatroomInfoCollectionRepository, redisMessageService);

        chatroomInfoCollection = ChatroomInfoCollection.builder()
                .senderNo(1L)
                .receiverNo(2L)
                .chatroomType(ChatroomType.PRIVATE)
                .build();

        messageDto = ChatMessageRequestDTO.builder()
                .senderNo(1L)
                .receiverNo(2L)
                .message("message")
                .build();
    }

    @Test
    @DisplayName("processMessage -  채팅방이 별도로 존재하지 않는 경우 NotFoundException 발생")
    void When_processMessage_IfChatroomNotExist_Expect_NotFoundException() {
        // When - Then
        assertThatThrownBy(() -> chatMessageService.processMessage(messageDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("해당하는 채팅방을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("processMessage - 채팅방이 존재한 상태에서 메시지를 전송함을 테스트")
    void When_processMessage_IfChatroomExist_Expect_SendMessage() {
        // Given
        mongoTemplate.save(chatroomInfoCollection);

        // When
        chatMessageService.processMessage(messageDto);

        // Then
        ChatroomInfoCollection foundChatroom = chatroomInfoCollectionRepository.findBySenderNoAndReceiverNo(1L, 2L).get();
        ChatMessageCollection foundChatMessage = chatMessageCollectionRepository.findAll().get(0);

        assertThat(foundChatroom).isNotNull();
        assertThat(foundChatroom.getChatroomType()).isEqualTo(ChatroomType.PRIVATE);

        assertThat(foundChatMessage).isNotNull();
        assertThat(foundChatMessage.getSenderNo()).isEqualTo(1L);
        assertThat(foundChatMessage.getMessage()).isEqualTo("message");
        assertThat(foundChatMessage.getReceiverNo()).isEqualTo(2L);
    }

    @AfterEach
    void tearDown() {
        for (String collectionName : mongoTemplate.getCollectionNames()) {
            mongoTemplate.dropCollection(collectionName);
        }
    }
}