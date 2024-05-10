package com.st.eighteen_be.chat.service.redis;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * packageName    : com.st.eighteen_be.chat.service.redis
 * fileName       : RedisMessageService
 * author         : ipeac
 * date           : 24. 5. 10.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 5. 10.        ipeac       최초 생성
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RedisMessageService {
    private static final String UNREAD_COUNT_REDIS_KEY = "unreadMessageCount:";
    private final RedisTemplate<String, Object> redisTemplate;

    public void incrementUnreadMessageCount(@Nonnull Long receiverNo) {
        log.info("========== incrementUnreadMessageCount ========== receiverNo : {}", receiverNo);

        redisTemplate.opsForValue().increment(UNREAD_COUNT_REDIS_KEY + receiverNo);
    }

    public void resetUnreadMessageCount(@Nonnull Long receiverNo) {
        log.info("========== resetUnreadMessageCount ========== receiverNo : {}", receiverNo);

        redisTemplate.delete(UNREAD_COUNT_REDIS_KEY + receiverNo);
    }

    public int getUnreadMessageCount(String chatroomId, Long userNo) {
        log.info("========== getUnreadMessageCount ========== chatroomId : {}, userNo : {}", chatroomId, userNo);

        return (int) redisTemplate.opsForValue().get(UNREAD_COUNT_REDIS_KEY + userNo);
    }
}