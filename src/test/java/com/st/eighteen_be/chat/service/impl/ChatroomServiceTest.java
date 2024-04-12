package com.st.eighteen_be.chat.service.impl;

import com.st.eighteen_be.chat.model.collection.ChatroomInfoCollection;
import com.st.eighteen_be.chat.model.vo.ChatroomType;
import com.st.eighteen_be.chat.repository.ChatroomInfoCollectionRepository;
import com.st.eighteen_be.common.annotation.ServiceWithMongoDBTest;
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
 * fileName       : ChatroomServiceTest
 * author         : ipeac
 * date           : 24. 4. 12.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 4. 12.        ipeac       최초 생성
 */
@ServiceWithMongoDBTest
class ChatroomServiceTest {
    @Autowired
    private MongoTemplate mongoTemplate;
    
    private ChatroomService chatroomService;
    
    @Autowired
    private ChatroomInfoCollectionRepository chatroomInfoCollectionRepository;
    
    private ChatroomInfoCollection savedChatroomInfoCollection;
    
    @BeforeEach
    void setUp() {
        chatroomService = new ChatroomService(chatroomInfoCollectionRepository);
        
        savedChatroomInfoCollection = ChatroomInfoCollection.of(1L, 1L, ChatroomType.PRIVATE);
    }
    
    @Test
    @DisplayName("채팅방 정상 생성 테스트 - 올바른 파라미터")
    void When_CreateChatroom_Then_Success() {
        // When
        chatroomService.createChatroom(1L, 1L);
        
        // Then
        ChatroomInfoCollection found = chatroomInfoCollectionRepository.findByPostNoAndMemberNo(1L, 1L).get();
        assertThat(found).isNotNull();
        assertThat(found.getPostNo()).isEqualTo(1L);
        assertThat(found.getMemberNo()).isEqualTo(1L);
    }
    
    @Test
    @DisplayName("채팅방 파라미터 오류 테스트 - postNo가 null or MemberNo가 null")
    void When_CreateChatroom_Then_Exception_PostNoIsNull() {
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
        ChatroomInfoCollection found = chatroomService.getChatroom(1L, 1L).get();
        
        // Then
        assertThat(found).isNotNull();
        assertThat(found.getPostNo()).isEqualTo(1L);
        assertThat(found.getMemberNo()).isEqualTo(1L);
    }
    
    @Test
    @DisplayName("채팅방이 존재하지 않는 경우 Optional.empty() 반환")
    void When_GetChatroom_Then_Empty() {
        // When
        var found = chatroomService.getChatroom(1L, 1L);
        
        // Then
        assertThat(found).isEmpty();
    }
    
    @AfterEach
    void tearDown() {
        for (String collectionName : mongoTemplate.getCollectionNames()) {
            mongoTemplate.dropCollection(collectionName);
        }
    }
}