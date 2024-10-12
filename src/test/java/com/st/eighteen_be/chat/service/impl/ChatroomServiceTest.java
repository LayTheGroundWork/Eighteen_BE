package com.st.eighteen_be.chat.service.impl;

import com.st.eighteen_be.chat.model.collection.ChatMessageCollection;
import com.st.eighteen_be.chat.model.collection.ChatroomInfoCollection;
import com.st.eighteen_be.chat.model.dto.request.FindChatRoomRequestDTO;
import com.st.eighteen_be.chat.model.dto.response.ChatroomWithLastestMessageDTO;
import com.st.eighteen_be.chat.model.redishash.UnreadMessageCount;
import com.st.eighteen_be.chat.model.vo.ChatroomType;
import com.st.eighteen_be.chat.repository.mongo.ChatroomInfoCollectionRepository;
import com.st.eighteen_be.chat.repository.redis.UnreadMessageRedisRepository;
import com.st.eighteen_be.chat.service.ChatroomService;
import com.st.eighteen_be.chat.service.redis.RedisMessageService;
import com.st.eighteen_be.common.exception.sub_exceptions.data_exceptions.BadRequestException;
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
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

/**
 * packageName    : com.st.eighteen_be.chat.service.impl
 * fileName       : ChatroomServiceTest
 * author         : ipeac
 * date           : 24. 4. 12.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 4. 12.        ipeac       최초 생성
 */
@DisplayName("ChatroomService 테스트")
@ActiveProfiles("test")
@ExtendWith({MongodbTestContainerExtenstion.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ChatroomServiceTest extends RedisTestContainerExtenstion {
    
    //로거
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ChatroomServiceTest.class);
    public static final String SENDER_ID_TESTER = "senderIdTester";
    public static final String RECEIVER_ID_TESTER_2 = "receiverIdTester2";
    public static final String RECEIVER_ID_TESTER = "receiverIdTester";
    
    @Autowired
    private MongoTemplate mongoTemplate;
    
    private ChatroomService chatroomService;
    
    private RedisMessageService redisMessageService;
    
    @Autowired
    private UnreadMessageRedisRepository unreadMessageRedisRepository;
    
    @Autowired
    private ChatroomInfoCollectionRepository chatroomInfoCollectionRepository;
    
    private ChatroomInfoCollection savedChatroomInfoCollection;
    
    @BeforeEach
    void setUp() {
        redisMessageService = new RedisMessageService(unreadMessageRedisRepository);
        
        chatroomService = new ChatroomService(chatroomInfoCollectionRepository, redisMessageService);
        
        savedChatroomInfoCollection = ChatroomInfoCollection.of(SENDER_ID_TESTER, RECEIVER_ID_TESTER, ChatroomType.PRIVATE);
    }
    
    @Test
    @DisplayName("채팅방 정상 생성 테스트 - 올바른 파라미터")
    void When_CreateChatroom_Then_Success() {
        // When
        ChatroomInfoCollection actual = chatroomService.createChatroom(SENDER_ID_TESTER, RECEIVER_ID_TESTER);
        
        // Then
        ChatroomInfoCollection found = chatroomInfoCollectionRepository.findById(actual.get_id().toString()).get();
        assertThat(found).isNotNull();
        assertThat(found.getSenderId()).isEqualTo(SENDER_ID_TESTER);
        assertThat(found.getReceiverId()).isEqualTo(RECEIVER_ID_TESTER);
    }
    
    @Test
    @DisplayName("채팅방 파라미터 오류 테스트 - 매개변수 null 테스트")
    void When_CreateChatroom_Then_Exception_NullPointerException() {
        // When & Then
        assertThatThrownBy(() -> chatroomService.createChatroom(null, RECEIVER_ID_TESTER))
                .isInstanceOf(NullPointerException.class);
        
        assertThatThrownBy(() -> chatroomService.createChatroom(SENDER_ID_TESTER, null))                .isInstanceOf(NullPointerException.class);
    }
    
    @Test
    @DisplayName("채팅방 조회 테스트 - 존재하는 채팅방 조회")
    void When_GetChatroom_Then_Success() {
        // Given
        ChatroomInfoCollection saved = mongoTemplate.save(savedChatroomInfoCollection);
        
        // When
        ChatroomInfoCollection found = chatroomService.getChatroom(saved.get_id().toString()).get();
        
        // Then
        assertThat(found).isNotNull();
        assertThat(found.getSenderId()).isEqualTo(SENDER_ID_TESTER);
        assertThat(found.getReceiverId()).isEqualTo(RECEIVER_ID_TESTER);
    }
    
    @Test
    @DisplayName("채팅방 생성시 - 송신자와 수신자가 동일한 경우 BadRequestException 발생")
    void When_CreateChatroom_Then_Exception_BadRequestException() {
        // When & Then
        assertThatThrownBy(() -> chatroomService.createChatroom(SENDER_ID_TESTER, SENDER_ID_TESTER))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("같은 사용자입니다.");
    }
    
    @Test
    @DisplayName("채팅방이 존재하지 않는 경우 Optional.empty() 반환")
    void When_GetChatroom_Then_Empty() {
        // When
        var found = chatroomService.getChatroom("123456789012345678901234");
        
        // Then
        assertThat(found).isEmpty();
    }
    
    @Test
    @DisplayName("채팅방 전체 목록 조회  - 각 채팅방의  마지막 채팅까지 올바르게 가져와야합니다.")
    void When_FindAllMyChatrooms_Then_Success() {
        // Given
        ChatroomInfoCollection anotherSavedChatroomInfoCollection = ChatroomInfoCollection.of(SENDER_ID_TESTER, RECEIVER_ID_TESTER_2, ChatroomType.PRIVATE);
        
        FindChatRoomRequestDTO requestDTO = FindChatRoomRequestDTO.builder()
                .senderId(SENDER_ID_TESTER)
                .build();
        
        //채팅방 생성
        ChatroomInfoCollection firstChatroomInfo = mongoTemplate.save(savedChatroomInfoCollection);// 1 - 2 채팅방 생성
        ChatroomInfoCollection secondChatroomInfo = mongoTemplate.save(anotherSavedChatroomInfoCollection);// 1 - 3 채팅방 생성
        
        ChatMessageCollection firstChatMessage = ChatMessageCollection.builder()
                                                                .senderId(SENDER_ID_TESTER)
                                                                .receiverId(RECEIVER_ID_TESTER)
                                                                .chatroomInfoId(firstChatroomInfo.get_id())
                                                                .message("안녕하세요 1-2")
                                                                .createdAt(LocalDateTime.now().minusDays(1))
                                                                .build();
        
        ChatMessageCollection secondChatMessage = ChatMessageCollection.builder()
                                                                .senderId(SENDER_ID_TESTER)
                                                                .receiverId(RECEIVER_ID_TESTER_2)
                                                                .chatroomInfoId(secondChatroomInfo.get_id())
                                                                .message("안녕하세요 1-3")
                                                                .createdAt(LocalDateTime.now().minusDays(2))
                                                                .build();
        
        //채팅방별 채팅생성
        mongoTemplate.save(firstChatMessage);
        mongoTemplate.save(secondChatMessage);
        
        //redis 에 읽지 않은 메시지 카운트 저장
        unreadMessageRedisRepository.save(UnreadMessageCount.forChatroomEntry(RECEIVER_ID_TESTER, SENDER_ID_TESTER, 5L));
        unreadMessageRedisRepository.save(UnreadMessageCount.forChatroomEntry(RECEIVER_ID_TESTER_2, SENDER_ID_TESTER, 10L));
        
        // When
        var chatrooms = chatroomService.findAllMyChatrooms(requestDTO);
        
        // Then
        assertThat(chatrooms).isNotEmpty();
        
        assertSoftly(softAssertions -> {
            log.info("chatrooms.message : {}", chatrooms.get(0).getMessage());
            log.info("chatrooms.messageCreatedAt : {}", chatrooms.get(0).getMessageCreatedAt());
            
            softAssertions.assertThat(chatrooms.get(0).getSenderId()).isEqualTo(SENDER_ID_TESTER);
            softAssertions.assertThat(chatrooms.get(0).getReceiverId()).isEqualTo(RECEIVER_ID_TESTER);
            softAssertions.assertThat(chatrooms.get(0).getMessage()).isEqualTo("안녕하세요 1-2");
            softAssertions.assertThat(chatrooms.get(0).getUnreadMessageCount()).isEqualTo(5L);
            softAssertions.assertThat(chatrooms.get(0).getMessageCreatedAt()).isNotNull();
        });
        
        assertSoftly(softAssertions -> {
            log.info("chatrooms.message : {}", chatrooms.get(1).getMessage());
            log.info("chatrooms.messageCreatedAt : {}", chatrooms.get(1).getMessageCreatedAt());
            
            softAssertions.assertThat(chatrooms.get(1).getSenderId()).isEqualTo(SENDER_ID_TESTER);
            softAssertions.assertThat(chatrooms.get(1).getReceiverId()).isEqualTo(RECEIVER_ID_TESTER_2);
            softAssertions.assertThat(chatrooms.get(1).getMessage()).isEqualTo("안녕하세요 1-3");
            softAssertions.assertThat(chatrooms.get(1).getUnreadMessageCount()).isEqualTo(10L);
            softAssertions.assertThat(chatrooms.get(1).getMessageCreatedAt()).isNotNull();
        });
    }
    
    @Test
    @DisplayName("채팅방 나가기 테스트 - 채팅방 나가기 성공")
    void When_QuitChatroom_Then_Success() {
        // Given
        ChatroomInfoCollection saved = mongoTemplate.save(savedChatroomInfoCollection);
        
        // When
        chatroomService.quitChatroom(saved.get_id().toString(), SENDER_ID_TESTER);
        
        // Then
        ChatroomInfoCollection found = chatroomInfoCollectionRepository.findById(saved.get_id().toString()).get();
        assertThat(found.getLeftUsers()).contains(SENDER_ID_TESTER);
    }
    
    @Test
    @DisplayName("채팅방 나가기 테스트 - 채팅방 나가기 성공 후 조회시 조회되지 않아야 한다.")
    void When_QuitChatroom_Then_NotFound() {
        // Given
        ChatroomInfoCollection saved = mongoTemplate.save(savedChatroomInfoCollection);
        
        // When
        chatroomService.quitChatroom(saved.get_id().toString(), savedChatroomInfoCollection.getSenderId());
        
        // Then
        List<ChatroomWithLastestMessageDTO> allChatroomBySenderNo = chatroomInfoCollectionRepository.findAllChatroomBySenderNo(savedChatroomInfoCollection.getSenderId());
        assertThat(allChatroomBySenderNo).isEmpty();
    }
    
    @AfterEach
    void tearDown() {
        for (String collectionName : mongoTemplate.getCollectionNames()) {
            mongoTemplate.dropCollection(collectionName);
        }
    }
}
