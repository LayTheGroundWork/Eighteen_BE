package com.st.eighteen_be.chat.service.redis;

import com.st.eighteen_be.chat.model.redishash.UnreadMessageCount;
import com.st.eighteen_be.chat.repository.redis.UnreadMessageRedisRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
@Transactional(readOnly = true)
@Slf4j
public class RedisMessageService {
    
    private final UnreadMessageRedisRepository unreadMessageRedisRepository;
    
    @Transactional(readOnly = false)
    public void incrementUnreadMessageCount(
            @NotNull(message = "senderNo must not be null") @NotNull Long senderNo,
            @NotNull(message = "receiverNo must not be null") @NotNull Long receiverNo
    ) {
        log.info("========== incrementUnreadMessageCount ========== senderNo : {}, receiverNo : {}", senderNo, receiverNo);
        UnreadMessageCount unreadMessageCount = UnreadMessageCount.forChatroomEntry(senderNo, receiverNo, 1L);
        
        unreadMessageRedisRepository.findById(unreadMessageCount.getId())
                .ifPresentOrElse(
                        unreadMessage -> {
                            log.info("========== redisCount updating ========== unreadMessage : {}", unreadMessage.getId());
                            
                            unreadMessage.incrementCount();
                            unreadMessageRedisRepository.save(unreadMessage);
                        },
                        () -> unreadMessageRedisRepository.save(unreadMessageCount)
                );
    }
    
    @Transactional(readOnly = false)
    public void resetUnreadMessageCount(UnreadMessageCount unreadMessageCount) {
        log.info("========== resetUnreadMessageCount ========== unreadMessageCount : {}", unreadMessageCount.getId());
        
        unreadMessageRedisRepository.delete(unreadMessageCount);
    }
    
    public long getUnreadMessageCount(
            @NotNull(message = "myNo must not be null") @NotNull Long myNo,
            @NotNull(message = "otherNo must not be null") @NotNull Long otherNo
    ) {
        log.info("========== getUnreadMessageCount ========== myNo : {}, otherNo : {}", myNo, otherNo);
        
        String combinedId = UnreadMessageCount.makeId(otherNo, myNo);
        
        return unreadMessageRedisRepository.findById(combinedId)
                .map(UnreadMessageCount::getCount)
                .orElseGet(() -> {
                    UnreadMessageCount unreadMessageCount = UnreadMessageCount.forChatroomEntry(otherNo, myNo, 0L);
                    unreadMessageRedisRepository.save(unreadMessageCount);
                    return unreadMessageCount.getCount();
                });
    }
}