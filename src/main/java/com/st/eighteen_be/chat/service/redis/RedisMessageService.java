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
    
    /**
     * <pre>
     *  채팅방 메시지 수신 개수 증가
     *  (주의 - 메시지 송신자는 수신자에게 메시지를 전송하는데  수신자의 송신자에 대한 읽지않음 개수를 하나 더한다.)
     * </pre>
     *
     * @param senderId   채팅방 송신자 ID
     * @param receiverId 채팅방 수신자 ID
     */
    @Transactional(readOnly = false)
    public void incrementUnreadMessageCount(
            @NotNull(message = "senderId must not be null") @NotNull String senderId,
            @NotNull(message = "receiverId must not be null") @NotNull String receiverId
    ) {
        log.info("========== incrementUnreadMessageCount ========== senderId : {}, receiverId : {}", senderId, receiverId);
        UnreadMessageCount unreadMessageCount = UnreadMessageCount.forChatroomEntry(senderId, receiverId, 1L);
        unreadMessageRedisRepository.findById(unreadMessageCount.getId())
                .ifPresentOrElse(
                        unreadMessage -> {
                            unreadMessage.incrementCount();
                            unreadMessageRedisRepository.save(unreadMessage);
                        },
                        () -> unreadMessageRedisRepository.save(unreadMessageCount)
                );
    }
    
    @Transactional(readOnly = false)
    public void resetUnreadMessageCount(UnreadMessageCount unreadMessageCount) {
        unreadMessageRedisRepository.delete(unreadMessageCount);
    }
    public long getUnreadMessageCount(
            @NotNull(message = "myId must not be null") @NotNull String myId,
            @NotNull(message = "otherId must not be null") @NotNull String otherId
    ) {
        log.info("========== getUnreadMessageCount ========== myId : {}, otherId : {}", myId, otherId);
        String combinedId = UnreadMessageCount.makeId(otherId, myId);
        return unreadMessageRedisRepository.findById(combinedId)
                .map(UnreadMessageCount::getCount)
                .orElseGet(() -> {
                    UnreadMessageCount unreadMessageCount = UnreadMessageCount.forChatroomEntry(otherId, myId, 0L);
                    unreadMessageRedisRepository.save(unreadMessageCount);
                    return unreadMessageCount.getCount();
                });
    }
}
