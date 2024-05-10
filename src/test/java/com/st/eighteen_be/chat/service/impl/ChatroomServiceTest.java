package com.st.eighteen_be.chat.service.impl;

import com.st.eighteen_be.chat.model.collection.ChatMessageCollection;
import com.st.eighteen_be.chat.model.collection.ChatroomInfoCollection;
import com.st.eighteen_be.chat.model.dto.request.FindChatRoomRequestDTO;
import com.st.eighteen_be.chat.model.vo.ChatroomType;
import com.st.eighteen_be.chat.repository.mongo.ChatroomInfoCollectionRepository;
import com.st.eighteen_be.chat.service.ChatroomService;
import com.st.eighteen_be.common.annotation.ServiceWithMongoDBTest;
import com.st.eighteen_be.common.exception.sub_exceptions.data_exceptions.BadRequestException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.time.LocalDateTime;

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
@ServiceWithMongoDBTest
class ChatroomServiceTest {

    //로거
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ChatroomServiceTest.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    private ChatroomService chatroomService;

    @Autowired
    private ChatroomInfoCollectionRepository chatroomInfoCollectionRepository;

    private ChatroomInfoCollection savedChatroomInfoCollection;

    @BeforeEach
    void setUp() {
        chatroomService = new ChatroomService(chatroomInfoCollectionRepository);

        savedChatroomInfoCollection = ChatroomInfoCollection.of(1L, 2L, ChatroomType.PRIVATE);
    }

    @Test
    @DisplayName("채팅방 정상 생성 테스트 - 올바른 파라미터")
    void When_CreateChatroom_Then_Success() {
        // When
        chatroomService.createChatroom(1L, 2L);

        // Then
        ChatroomInfoCollection found = chatroomInfoCollectionRepository.findBySenderNoAndReceiverNo(1L, 2L).get();
        assertThat(found).isNotNull();
        assertThat(found.getSenderNo()).isEqualTo(1L);
        assertThat(found.getReceiverNo()).isEqualTo(2L);
    }

    @Test
    @DisplayName("채팅방 파라미터 오류 테스트 - 매개변수 null 테스트")
    void When_CreateChatroom_Then_Exception_NullPointerException() {
        // When & Then
        assertThatThrownBy(() -> chatroomService.createChatroom(null, 1L))
                .isInstanceOf(NullPointerException.class);

        assertThatThrownBy(() -> chatroomService.createChatroom(1L, null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("채팅방 조회 테스트 - 존재하는 채팅방 조회")
    void When_GetChatroom_Then_Success() {
        // Given
        mongoTemplate.save(savedChatroomInfoCollection);

        // When
        ChatroomInfoCollection found = chatroomService.getChatroom(1L, 2L).get();

        // Then
        assertThat(found).isNotNull();
        assertThat(found.getSenderNo()).isEqualTo(1L);
        assertThat(found.getReceiverNo()).isEqualTo(2L);
    }

    @Test
    @DisplayName("채팅방 생성시 - 송신자와 수신자가 동일한 경우 BadRequestException 발생")
    void When_CreateChatroom_Then_Exception_BadRequestException() {
        // When & Then
        assertThatThrownBy(() -> chatroomService.createChatroom(1L, 1L))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("같은 사용자입니다.");
    }

    @Test
    @DisplayName("채팅방 조회 테스트 - 송신자 수신자 동일한 채팅방 조회시 BadRequestException 발생")
    void When_GetChatroom_Then_Exception_BadRequestException() {
        // When & Then
        assertThatThrownBy(() -> chatroomService.getChatroom(1L, 1L))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("같은 사용자입니다.");
    }

    @Test
    @DisplayName("채팅방이 존재하지 않는 경우 Optional.empty() 반환")
    void When_GetChatroom_Then_Empty() {
        // When
        var found = chatroomService.getChatroom(1L, 2L);

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("채팅방 전체 목록 조회  - 각 채팅방의  마지막 채팅까지 올바르게 가져와야합니다.")
    void When_FindAllMyChatrooms_Then_Success() {
        // Given
        ChatroomInfoCollection anotherSavedChatroomInfoCollection = ChatroomInfoCollection.of(1L, 3L, ChatroomType.PRIVATE);

        FindChatRoomRequestDTO requestDTO = FindChatRoomRequestDTO.builder()
                .senderNo(1L)
                .build();

        //채팅방 생성
        ChatroomInfoCollection firstChatroomInfo = mongoTemplate.save(savedChatroomInfoCollection);// 1 - 2 채팅방 생성
        ChatroomInfoCollection secondChatroomInfo = mongoTemplate.save(anotherSavedChatroomInfoCollection);// 1 - 3 채팅방 생성

        ChatMessageCollection firstChatMessage = ChatMessageCollection.builder()
                .senderNo(1L)
                .receiverNo(2L)
                .chatroomInfoId(firstChatroomInfo.get_id())
                .message("안녕하세요 1-2")
                .createdAt(LocalDateTime.now().minusDays(1))
                .build();

        ChatMessageCollection secondChatMessage = ChatMessageCollection.builder()
                .senderNo(1L)
                .receiverNo(3L)
                .chatroomInfoId(secondChatroomInfo.get_id())
                .message("안녕하세요 1-3")
                .createdAt(LocalDateTime.now().minusDays(2))
                .build();

        //채팅방별 채팅생성
        mongoTemplate.save(firstChatMessage);
        mongoTemplate.save(secondChatMessage);

        // When
        var chatrooms = chatroomService.findAllMyChatrooms(requestDTO);

        // Then
        assertThat(chatrooms).isNotEmpty();


        assertSoftly(softAssertions -> {
            log.info("chatrooms.message : {}", chatrooms.get(0).getMessage());
            log.info("chatrooms.messageCreatedAt : {}", chatrooms.get(0).getMessageCreatedAt());

            softAssertions.assertThat(chatrooms.get(0).getSenderNo()).isEqualTo(1L);
            softAssertions.assertThat(chatrooms.get(0).getReceiverNo()).isEqualTo(2L);
            softAssertions.assertThat(chatrooms.get(0).getMessage()).isEqualTo("안녕하세요 1-2");
            softAssertions.assertThat(chatrooms.get(0).getMessageCreatedAt()).isNotNull();
        });

        assertSoftly(softAssertions -> {
            log.info("chatrooms.message : {}", chatrooms.get(1).getMessage());
            log.info("chatrooms.messageCreatedAt : {}", chatrooms.get(1).getMessageCreatedAt());

            softAssertions.assertThat(chatrooms.get(1).getSenderNo()).isEqualTo(1L);
            softAssertions.assertThat(chatrooms.get(1).getReceiverNo()).isEqualTo(3L);
            softAssertions.assertThat(chatrooms.get(1).getMessage()).isEqualTo("안녕하세요 1-3");
            softAssertions.assertThat(chatrooms.get(1).getMessageCreatedAt()).isNotNull();
        });
    }

    @AfterEach
    void tearDown() {
        for (String collectionName : mongoTemplate.getCollectionNames()) {
            mongoTemplate.dropCollection(collectionName);
        }
    }
}