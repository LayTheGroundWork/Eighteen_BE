package com.st.eighteen_be.chat.model.dto.response;

import com.st.eighteen_be.chat.model.collection.ChatroomInfoCollection;
import com.st.eighteen_be.chat.model.vo.ChatroomType;
import lombok.Builder;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link ChatroomInfoCollection}
 */
public record ChatroomWithLastestMessageDTO(
        Long senderNo,
        Long receiverNo,
        ChatroomType chatroomType,
        ChatMessageResponseDTO lastestMessage,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) implements Serializable {
    @Builder
    public ChatroomWithLastestMessageDTO {
    }
}