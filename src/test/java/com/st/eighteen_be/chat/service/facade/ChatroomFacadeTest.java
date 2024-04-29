package com.st.eighteen_be.chat.service.facade;

import com.st.eighteen_be.chat.model.collection.ChatMessageCollection;
import com.st.eighteen_be.chat.model.collection.ChatroomInfoCollection;
import com.st.eighteen_be.chat.model.dto.request.EnterChatRoomRequestDTO;
import com.st.eighteen_be.chat.model.dto.response.ChatMessageResponseDTO;
import com.st.eighteen_be.chat.model.vo.ChatroomType;
import com.st.eighteen_be.chat.repository.mongo.ChatMessageCollectionRepository;
import com.st.eighteen_be.chat.repository.mongo.ChatroomInfoCollectionRepository;
import com.st.eighteen_be.chat.service.impl.ChatMessageService;
import com.st.eighteen_be.chat.service.impl.ChatroomService;
import com.st.eighteen_be.common.annotation.ServiceWithMongoDBTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * packageName    : com.st.eighteen_be.chat.service.facade
 * fileName       : ChatroomFacadeTest
 * author         : ipeac
 * date           : 24. 4. 12.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 4. 12.        ipeac       최초 생성
 */
@ServiceWithMongoDBTest
public class ChatroomFacadeTest {
    
    @Autowired
    private MongoTemplate mongoTemplate;
    
    private ChatroomFacade chatroomFacade;
    
    private ChatroomService chatroomService;
    
    private ChatMessageService chatMessageService;
    
    @Autowired
    private ChatMessageCollectionRepository chatMessageCollectionRepository;
    
    @Autowired
    private ChatroomInfoCollectionRepository chatroomInfoCollectionRepository;
    
    private ChatroomInfoCollection chatroomInfoCollection;
    
    private ChatMessageCollection chatMessageCollection;
    
    private EnterChatRoomRequestDTO enterChatRoomRequestDTO;
    
    @BeforeEach
    void setUp() {
        // Given
        chatroomService = new ChatroomService(chatroomInfoCollectionRepository);
        chatMessageService = new ChatMessageService(chatMessageCollectionRepository, chatroomInfoCollectionRepository);
        chatroomFacade = new ChatroomFacade(chatroomService, chatMessageService);
        
        chatroomInfoCollection = ChatroomInfoCollection.builder()
                .roomId("1")
                .senderNo(1L)
                .receiverNo(2L)
                .chatroomType(ChatroomType.PRIVATE)
                .build();
        
        chatMessageCollection = ChatMessageCollection.builder()
                .roomId("1")
                .sender(1L)
                .receiver(2L)
                .createdAt(LocalDateTime.now().minusDays(1))
                .updatedAt(LocalDateTime.now().minusDays(1))
                .message("Hello")
                .build();
        
        enterChatRoomRequestDTO = EnterChatRoomRequestDTO.builder()
                .senderNo(1L)
                .receiverNo(2L)
                .requestTime(LocalDateTime.now())
                .build();
    }
    
    @Test
    @DisplayName("채팅메시지가 저장된 내역이 있는 경우 메시지 정상 반환")
    void When_findMessagesBeforeTimeInRoom_Expect_EmptyList() {
        //Given
        mongoTemplate.save(chatroomInfoCollection);
        mongoTemplate.save(chatMessageCollection);
        
        // When
        List<ChatMessageResponseDTO> actual = chatroomFacade.getOrCreateChatroom(enterChatRoomRequestDTO);
        
        // Then
        assertThat(actual).isNotEmpty();
        assertThat(actual.get(0).sender()).isEqualTo(1L);
        assertThat(actual.get(0).receiver()).isEqualTo(2L);
        assertThat(actual.get(0).message()).isEqualTo("Hello");
    }
    
    @Test
    @DisplayName("채팅메시지가 저장된 내역이 없는 경우 빈 리스트 반환")
    void When_findMessagesBeforeTimeInRoom_Expect_NotEmptyList() {
        //Given
        mongoTemplate.save(chatroomInfoCollection);
        
        // When
        List<ChatMessageResponseDTO> actual = chatroomFacade.getOrCreateChatroom(enterChatRoomRequestDTO);
        
        // Then
        assertThat(actual).isEmpty();
    }
    
    
    @AfterEach
    void tearDown() {
        for (String collectionName : mongoTemplate.getCollectionNames()) {
            mongoTemplate.dropCollection(collectionName);
        }
    }
}