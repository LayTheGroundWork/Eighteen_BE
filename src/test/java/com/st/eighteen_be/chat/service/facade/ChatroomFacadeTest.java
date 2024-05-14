package com.st.eighteen_be.chat.service.facade;

import com.st.eighteen_be.chat.model.collection.ChatMessageCollection;
import com.st.eighteen_be.chat.model.collection.ChatroomInfoCollection;
import com.st.eighteen_be.chat.model.dto.request.EnterChatRoomRequestDTO;
import com.st.eighteen_be.chat.model.dto.response.ChatMessageResponseDTO;
import com.st.eighteen_be.chat.model.redishash.UnreadMessageCount;
import com.st.eighteen_be.chat.model.vo.ChatroomType;
import com.st.eighteen_be.chat.repository.mongo.ChatMessageCollectionRepository;
import com.st.eighteen_be.chat.repository.mongo.ChatroomInfoCollectionRepository;
import com.st.eighteen_be.chat.repository.redis.UnreadMessageRedisRepository;
import com.st.eighteen_be.chat.service.ChatMessageService;
import com.st.eighteen_be.chat.service.ChatroomService;
import com.st.eighteen_be.chat.service.redis.RedisMessageService;
import com.st.eighteen_be.common.extension.MongodbTestContainerExtenstion;
import com.st.eighteen_be.common.extension.RedisTestContainerExtenstion;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
@DisplayName("ChatroomFacade 테스트")
@ExtendWith({MongodbTestContainerExtenstion.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ChatroomFacadeTest extends RedisTestContainerExtenstion {
    
    @Autowired
    private MongoTemplate mongoTemplate;
    
    private ChatroomFacade chatroomFacade;
    
    private RedisMessageService redisMessageService;
    
    private ChatroomService chatroomService;
    
    private ChatMessageService chatMessageService;
    
    @Autowired
    private UnreadMessageRedisRepository unreadMessageRedisRepository;
    
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
        redisMessageService = new RedisMessageService(unreadMessageRedisRepository);
        chatroomService = new ChatroomService(chatroomInfoCollectionRepository, redisMessageService);
        chatMessageService = new ChatMessageService(chatMessageCollectionRepository, chatroomInfoCollectionRepository, redisMessageService);
        chatroomFacade = new ChatroomFacade(chatroomService, chatMessageService, redisMessageService);
        
        chatroomInfoCollection = ChatroomInfoCollection.builder()
                .senderNo(1L)
                .receiverNo(2L)
                .chatroomType(ChatroomType.PRIVATE)
                .build();
        
        chatMessageCollection = ChatMessageCollection.builder()
                .senderNo(1L)
                .receiverNo(2L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .message("Hello")
                .build();
        
        enterChatRoomRequestDTO = EnterChatRoomRequestDTO.builder()
                .senderNo(1L)
                .receiverNo(2L)
                .requestTime(LocalDateTime.now().plusDays(1))
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
        assertThat(actual.get(0).getSenderNo()).isEqualTo(1L);
        assertThat(actual.get(0).getReceiverNo()).isEqualTo(2L);
        assertThat(actual.get(0).getMessage()).isEqualTo("Hello");
    }
    
    @Test
    @DisplayName("채팅방 조회시 REDIS 에서 읽지 않은 메시지 카운트 초기화")
    void When_getOrCreateChatroom_Expect_ResetUnreadMessageCount() {
        //Given
        unreadMessageRedisRepository.save(UnreadMessageCount.forChatroomEntry(1L, 2L, 1L));
        assertThat(unreadMessageRedisRepository.findById(UnreadMessageCount.makeId(1L, 2L))).isPresent();
        
        // When
        chatroomFacade.getOrCreateChatroom(enterChatRoomRequestDTO);
        
        // Then
        assertThat(unreadMessageRedisRepository.findById(UnreadMessageCount.makeId(1L, 2L))).isEmpty();
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