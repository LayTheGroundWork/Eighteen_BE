package com.st.eighteen_be.chat.service.redis;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Optional;

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
    private static final MessageFormat UNREAD_COUNT_REDIS_KEY_FORMAT = new MessageFormat("unreadMessageCount:{0}:{1}");
    private final RedisTemplate<String, Object> redisTemplate;

    public void incrementUnreadMessageCount(
            @NotNull(message = "senderNo can`t be null") Long senderNo,
            @NotNull(message = "receiverNo can`t be null") Long receiverNo
    ) {
        log.info("========== incrementUnreadMessageCount ========== senderNo : {}, receiverNo : {}", senderNo, receiverNo);

        redisTemplate.opsForValue().increment(getUnreadMessageKey(senderNo, receiverNo), 1);
    }

    public void resetUnreadMessageCount(
            @NotNull(message = "myNo can`t be null") Long myNo,
            @NotNull(message = "otherNo can`t be null") Long otherNo
    ) {
        log.info("========== resetUnreadMessageCount ========== myNo : {}, otherNo : {}", myNo, otherNo);

        redisTemplate.delete(getUnreadMessageKey(myNo, otherNo));
    }

    public long getUnreadMessageCount(
            @NotNull(message = "myNo can`t be null") Long myNo,
            @NotNull(message = "otherNo can`t be null") Long otherNo
    ) {
        log.info("========== getUnreadMessageCount ========== myNo : {}, otherNo : {}", myNo, otherNo);

        Optional<Long> countOps = Optional.ofNullable((Long) redisTemplate.opsForValue().get(getUnreadMessageKey(myNo, otherNo)));

        return countOps.orElse(0L);
    }

    private static String getUnreadMessageKey(Long myNo, Long otherNo) {
        return UNREAD_COUNT_REDIS_KEY_FORMAT.format(new Object[]{myNo, otherNo});
    }
}