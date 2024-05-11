package com.st.eighteen_be.chat.service.redis;

import com.st.eighteen_be.common.annotation.ServiceWithRedisTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

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
class RedisMessageServiceTest {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private RedisMessageService redisMessageService;

    @BeforeEach
    public void setUp() {
        // Given
        redisMessageService = new RedisMessageService(redisTemplate);
    }

    @Test
    @DisplayName("redis unreadMessageCount 증가 테스트")
    public void When_IncrementUnreadMessageCount_Expect_Success() {
        // When
        redisTemplate.opsForValue().set("unreadMessageCount:1:2", 0L);
        redisMessageService.incrementUnreadMessageCount(1L, 2L);

        // Then
        Long unreadCount = (Long) redisTemplate.opsForValue().get("unreadMessageCount:1:2");
        assertThat(unreadCount).isNotNull().isEqualTo(1);
    }

    @Test
    @DisplayName("redis unreadMessageCount 초기화 테스트")
    public void When_ResetUnreadMessageCount_Expect_Success() {
        // Given

        // When

        // Then
    }

    @Test
    @DisplayName("redis unreadMessageCount 조회 테스트")
    public void When_GetUnreadMessageCount_Expect_Success() {
        // Given

        // When

        // Then
    }
}