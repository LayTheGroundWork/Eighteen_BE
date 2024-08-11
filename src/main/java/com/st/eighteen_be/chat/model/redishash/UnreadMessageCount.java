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
    private Long senderNo;
    private Long receiverNo;
    private long count;
    
    @Builder
    public UnreadMessageCount(Long senderNo, Long receiverNo, Long count) {
        this.id = makeId(senderNo, receiverNo);
        this.senderNo = senderNo;
        this.receiverNo = receiverNo;
        this.count = count;
    }
    
    public void incrementCount() {
        this.count++;
    }
    
    public static UnreadMessageCount forChatroomEntry(Long senderNo, Long receiverNo, Long count) {
        return UnreadMessageCount.builder()
                .senderNo(senderNo)
                .receiverNo(receiverNo)
                .count(count)
                .build();
    }
    
    public static UnreadMessageCount forChatroomEntry(Long receiverNo, Long senderNo) {
        return UnreadMessageCount.builder()
                .senderNo(senderNo)
                .receiverNo(receiverNo)
                .count(0L)
                .build();
    }
    
    public static String makeId(Long senderNo, Long receiverNo) {
        return MessageFormat.format(UNREAD_MESSAGE_COUNT_KEY + ":{0}:{1}", senderNo, receiverNo);
    }
}