package com.st.eighteen_be.chat.model.dto.response;

import com.st.eighteen_be.chat.model.collection.ChatroomInfoCollection;
import com.st.eighteen_be.chat.model.vo.ChatroomType;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link ChatroomInfoCollection}
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatroomWithLastestMessageDTO implements Serializable {
    private Long senderNo;
    private Long receiverNo;
    private ChatroomType chatroomType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String message;
    private LocalDateTime messageCreatedAt;
    
    @Builder
    private ChatroomWithLastestMessageDTO(Long senderNo, Long receiverNo, ChatroomType chatroomType, LocalDateTime createdAt, LocalDateTime updatedAt, String message, LocalDateTime messageCreatedAt) {
        this.senderNo = senderNo;
        this.receiverNo = receiverNo;
        this.chatroomType = chatroomType;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.message = message;
        this.messageCreatedAt = messageCreatedAt;
    }
}