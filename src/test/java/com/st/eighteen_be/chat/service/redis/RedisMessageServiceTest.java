package com.st.eighteen_be.chat.service.redis;

import com.st.eighteen_be.chat.model.redishash.UnreadMessageCount;
import com.st.eighteen_be.chat.repository.redis.UnreadMessageRedisRepository;
import com.st.eighteen_be.common.annotation.ServiceWithRedisTest;
import com.st.eighteen_be.common.extension.RedisTestContainerExtenstion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * packageName    : com.st.eighteen_be.chat.service.redis
 * fileName       : RedisMessageServiceTest
 * author         : ipeac
 * date           : 24. 5. 11.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 5. 11.        ipeac       최초 생성
 */
@DisplayName("REDIS CRUD 테스트")
@ServiceWithRedisTest
class RedisMessageServiceTest extends RedisTestContainerExtenstion {
    
    @Autowired
    private UnreadMessageRedisRepository unreadMessageRedisRepository;
    
    private RedisMessageService redisMessageService;
    
    @BeforeEach
    public void setUp() {
        // Given
        redisMessageService = new RedisMessageService(unreadMessageRedisRepository);
    }
    
    @Test
    @DisplayName("읽지 않음 카운트가 REDIS 에 저장되어 있지 않은 상태에서 카운트 증가 테스트")
    void When_IncrementUnreadMessageCount_Expect_Success() {
        // When
        redisMessageService.incrementUnreadMessageCount(1L, 2L);
        
        // Then
        Optional<UnreadMessageCount> found = unreadMessageRedisRepository.findById(UnreadMessageCount.makeId(1L, 2L));
        assertThat(found).isPresent();
        assertThat(found.get().getCount()).isEqualTo(1);
    }
    
    @Test
    @DisplayName("읽지 않음 카운트가 REDIS 에 저장되어 있는 상태에서 카운트 증가 테스트")
    void When_IncrementUnreadMessageCount_Expect_Success2() {
        // Given
        UnreadMessageCount unreadMessageCount = UnreadMessageCount.forChatroomEntry(1L, 2L, 1L);
        unreadMessageRedisRepository.save(unreadMessageCount);
        
        // When
        redisMessageService.incrementUnreadMessageCount(1L, 2L);
        
        // Then
        Optional<UnreadMessageCount> found = unreadMessageRedisRepository.findById(UnreadMessageCount.makeId(1L, 2L));
        assertThat(found).isPresent();
        assertThat(found.get().getCount()).isEqualTo(2);
    }
    
    @Test
    @DisplayName("REDIS 읽지않음 초기화 테스트")
    void When_ResetUnreadMessageCount_Expect_Success() {
        // Given
        unreadMessageRedisRepository.save(UnreadMessageCount.forChatroomEntry(1L, 2L, 1L));
        
        // When
        redisMessageService.resetUnreadMessageCount(UnreadMessageCount.forChatroomEntry(1L, 2L, 1L));
        
        // Then
        Optional<UnreadMessageCount> found = unreadMessageRedisRepository.findById(UnreadMessageCount.makeId(1L, 2L));
        
        assertThat(found).isEmpty();
    }
    
    @Test
    @DisplayName("REDIS 읽지않음 카운트 조회 정상 테스트")
    void When_GetUnreadMessageCount_Expect_Success() {
        // Given
        unreadMessageRedisRepository.save(UnreadMessageCount.forChatroomEntry(1L, 2L, 10L));
        
        // When
        long unreadMessageCount = redisMessageService.getUnreadMessageCount(2L, 1L);
        
        // Then
        assertThat(unreadMessageCount).isEqualTo(10L);
    }
}