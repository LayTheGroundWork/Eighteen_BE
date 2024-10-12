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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

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
@ActiveProfiles("test")
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

    @MockBean
    private SimpMessagingTemplate messagingTemplate;

    @BeforeEach
    void setUp() {
        // Given
        redisMessageService = new RedisMessageService(unreadMessageRedisRepository);
        chatroomService = new ChatroomService(chatroomInfoCollectionRepository, redisMessageService);
        chatMessageService = new ChatMessageService(chatMessageCollectionRepository, chatroomInfoCollectionRepository, redisMessageService, messagingTemplate);
        chatroomFacade = new ChatroomFacade(chatroomService, chatMessageService, redisMessageService);
        
        chatroomInfoCollection = ChatroomInfoCollection.builder()
                                         .senderId("senderIdTester")
                                         .receiverId("receiverIdTester")
                                         .chatroomType(ChatroomType.PRIVATE)
                                         .build();
        
        chatMessageCollection = ChatMessageCollection.builder()
                                        .senderId("senderIdTester")
                                        .receiverId("receiverIdTester")
                                        .createdAt(LocalDateTime.now())
                                        .updatedAt(LocalDateTime.now())
                                        .message("Hello")
                                        .build();
        
        enterChatRoomRequestDTO = EnterChatRoomRequestDTO.builder()
                                          .requestTime(LocalDateTime.now().plusDays(1))
                                          .build();
    }

    @Test
    @DisplayName("채팅메시지가 저장된 내역이 있는 경우 메시지 정상 반환")
    void When_findMessagesBeforeTimeInRoom_Expect_EmptyList() {
        //Given
        ChatroomInfoCollection saved = mongoTemplate.save(chatroomInfoCollection);

        ReflectionTestUtils.setField(chatMessageCollection, "chatroomInfoId", saved.get_id());

        mongoTemplate.save(chatMessageCollection);

        enterChatRoomRequestDTO = EnterChatRoomRequestDTO.builder()
                .chatroomInfoId(saved.get_id().toString())
                .requestTime(LocalDateTime.now())
                .build();

        // When
        List<ChatMessageResponseDTO> actual = chatroomFacade.getChatroom(enterChatRoomRequestDTO);

        // Then
        assertThat(actual).isNotEmpty();
        assertThat(actual.get(0).getSenderId()).isEqualTo("senderIdTester");
        assertThat(actual.get(0).getReceiverId()).isEqualTo("receiverIdTester");
        assertThat(actual.get(0).getMessage()).isEqualTo("Hello");
    }

    @Test
    @DisplayName("채팅방 조회시 REDIS 에서 읽지 않은 메시지 카운트 초기화")
    void When_getChatroom_Expect_ResetUnreadMessageCount() {
        //Given
        ChatroomInfoCollection saved = mongoTemplate.save(chatroomInfoCollection);

        unreadMessageRedisRepository.save(UnreadMessageCount.forChatroomEntry("senderIdTester", "receiverIdTester", 1L));
        assertThat(unreadMessageRedisRepository.findById(UnreadMessageCount.makeId("senderIdTester", "receiverIdTester"))).isPresent();

        enterChatRoomRequestDTO = EnterChatRoomRequestDTO.builder()
                .chatroomInfoId(saved.get_id().toString())
                .requestTime(LocalDateTime.now())
                .build();

        // When
        chatroomFacade.getChatroom(enterChatRoomRequestDTO);

        // Then
        assertThat(unreadMessageRedisRepository.findById(UnreadMessageCount.makeId("senderIdTester", "receiverIdTester"))).isEmpty();
    }

    @Test
    @DisplayName("채팅메시지가 저장된 내역이 없는 경우 빈 리스트 반환")
    void When_findMessagesBeforeTimeInRoom_Expect_NotEmptyList() {
        //Given
        ChatroomInfoCollection saved = mongoTemplate.save(chatroomInfoCollection);

        enterChatRoomRequestDTO = EnterChatRoomRequestDTO.builder()
                .chatroomInfoId(saved.get_id().toString())
                .requestTime(LocalDateTime.now())
                .build();

        // When
        List<ChatMessageResponseDTO> actual = chatroomFacade.getChatroom(enterChatRoomRequestDTO);

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
