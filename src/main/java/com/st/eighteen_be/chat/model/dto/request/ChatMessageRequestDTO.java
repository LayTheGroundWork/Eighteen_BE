package com.st.eighteen_be.chat.model.dto.request;

import com.st.eighteen_be.chat.model.collection.ChatMessageCollection;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.bson.types.ObjectId;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Schema(description = "채팅 메시지 요청 DTO")
@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessageRequestDTO {
    
    @Schema(description = "발신자 번호", example = "senderUniqueId", requiredMode = REQUIRED)
    @NotNull(message = "senderId can`t be null")
    private String senderId;
    
    @Schema(description = "메시지", example = "안녕하세요", requiredMode = REQUIRED)
    @NotNull(message = "message can`t be null")
    @NotEmpty(message = "message can`t be empty")
    @NotBlank(message = "message can`t be blank")
    private String message;
    
    @Schema(description = "수신자 번호", example = "receiverUniqueId", requiredMode = REQUIRED)
    @NotNull(message = "receiverId can`t be null")
    private String receiverId;
    
    @Schema(description = "채팅방 정보 ID", example = "60f1b3b3b3b3b3b3b3b3b3b3", requiredMode = REQUIRED)
    private String chatroomInfoId;
    
    @Builder
    private ChatMessageRequestDTO(String senderId, String message, String receiverId, String chatroomInfoId) {
        this.senderId = senderId;
        this.message = message;
        this.receiverId = receiverId;
        this.chatroomInfoId = chatroomInfoId;
    }
    
    public ChatMessageCollection toCollection() {
        return ChatMessageCollection.builder()
                       .senderId(senderId)
                       .receiverId(receiverId)
                       .message(message)
                       .chatroomInfoId(new ObjectId(chatroomInfoId))
                       .build();
    }
}
