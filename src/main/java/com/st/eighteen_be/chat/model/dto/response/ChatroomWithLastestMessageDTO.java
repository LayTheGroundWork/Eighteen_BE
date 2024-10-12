package com.st.eighteen_be.chat.model.dto.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.st.eighteen_be.chat.model.collection.ChatroomInfoCollection;
import com.st.eighteen_be.chat.model.vo.ChatroomType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.bson.types.ObjectId;

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
    
    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "채팅방 ID", example = "6073f3b4b3b3b3b3b3b3b3b3")
    private ObjectId _id;
    
    @Schema(description = "발신자 번호", example = "senderIdTester")
    private String senderId;
    
    @Schema(description = "수신자 번호", example = "receiverIdTester")
    private String receiverId;
    
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
    private ChatroomWithLastestMessageDTO(ObjectId _id, String senderId, String receiverId, ChatroomType chatroomType, LocalDateTime createdAt, LocalDateTime updatedAt, Long unreadMessageCount, String message, LocalDateTime messageCreatedAt) {
        this._id = _id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.chatroomType = chatroomType;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.unreadMessageCount = unreadMessageCount;
        this.message = message;
        this.messageCreatedAt = messageCreatedAt;
    }
}
