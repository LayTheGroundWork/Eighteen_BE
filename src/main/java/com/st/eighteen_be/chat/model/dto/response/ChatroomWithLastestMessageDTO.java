package com.st.eighteen_be.chat.model.dto.response;

import com.st.eighteen_be.chat.model.collection.ChatroomInfoCollection;
import com.st.eighteen_be.chat.model.vo.ChatroomType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link ChatroomInfoCollection}
 */
@Schema(description = "채팅방 정보 응답 DTO")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatroomWithLastestMessageDTO implements Serializable {
    
    @Schema(description = "발신자 번호", example = "1")
    private Long senderNo;
    
    @Schema(description = "수신자 번호", example = "2")
    private Long receiverNo;
    
    @Schema(description = "채팅방 타입", example = "PRIVATE")
    private ChatroomType chatroomType;
    
    @Schema(description = "생성 시간", example = "2021-04-12T00:00:00")
    private LocalDateTime createdAt;
    
    @Schema(description = "수정 시간", example = "2021-04-12T00:00:00")
    private LocalDateTime updatedAt;
    
    @Schema(description = "읽지 않은 메시지 개수", example = "1")
    private Long unreadMessageCount;
    
    @Schema(description = "최신 메시지", example = "안녕하세요")
    private String message;
    
    @Schema(description = "최신 메시지 생성 시간", example = "2021-04-12T00:00:00")
    private LocalDateTime messageCreatedAt;
    
    @Builder
    private ChatroomWithLastestMessageDTO(Long senderNo, Long receiverNo, ChatroomType chatroomType, LocalDateTime createdAt, LocalDateTime updatedAt, Long unreadMessageCount, String message, LocalDateTime messageCreatedAt) {
        this.senderNo = senderNo;
        this.receiverNo = receiverNo;
        this.chatroomType = chatroomType;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.unreadMessageCount = unreadMessageCount;
        this.message = message;
        this.messageCreatedAt = messageCreatedAt;
    }
}