package com.st.eighteen_be.chat.model.redishash;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.text.MessageFormat;

/**
 * packageName    : com.st.eighteen_be.chat.model.redishash
 * fileName       : UnreadMessageCount
 * author         : ipeac
 * date           : 24. 5. 11.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 5. 11.        ipeac       최초 생성
 */
@Getter
@NoArgsConstructor
@RedisHash(value = "unreadMessageCount") // Time To Live => infinite
public class UnreadMessageCount {
    private static final String UNREAD_MESSAGE_COUNT_KEY = "unreadMessageCount";
    
    @Id
    private String id;
    private String senderId;
    private String receiverId;
    private long count;
    
    @Builder
    public UnreadMessageCount(String senderId, String receiverId, Long count) {
        this.id = makeId(senderId, receiverId);
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.count = count;
    }
    
    public void incrementCount() {
        this.count++;
    }
    
    public static UnreadMessageCount forChatroomEntry(String senderId, String receiverId, Long count) {
        return UnreadMessageCount.builder()
                .senderId(senderId)
                .receiverId(receiverId)
                .count(count)
                .build();
    }
    
    public static UnreadMessageCount forChatroomEntry(String receiverId, String senderId) {
        return UnreadMessageCount.builder()
                .senderId(senderId)
                .receiverId(receiverId)
                .count(0L)
                .build();
    }
    
    public static String makeId(String senderId, String receiverId) {
        return MessageFormat.format(UNREAD_MESSAGE_COUNT_KEY + ":{0}:{1}", senderId, receiverId);
    }
}
