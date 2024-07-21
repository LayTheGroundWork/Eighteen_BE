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
    
    @Schema(description = "발신자 번호", example = "1", requiredMode = REQUIRED)
    @NotNull(message = "senderNo can`t be null")
    private Long senderNo;
    
    @Schema(description = "메시지", example = "안녕하세요", requiredMode = REQUIRED)
    @NotNull(message = "message can`t be null")
    @NotEmpty(message = "message can`t be empty")
    @NotBlank(message = "message can`t be blank")
    private String message;
    
    @Schema(description = "수신자 번호", example = "2", requiredMode = REQUIRED)
    @NotNull(message = "receiverNo can`t be null")
    private Long receiverNo;
    
    @Schema(description = "채팅방 정보 ID", example = "60f1b3b3b3b3b3b3b3b3b3b3", requiredMode = REQUIRED)
    private String chatroomInfoId;
    
    @Builder
    private ChatMessageRequestDTO(Long senderNo, String message, Long receiverNo, String chatroomInfoId) {
        this.senderNo = senderNo;
        this.message = message;
        this.receiverNo = receiverNo;
        this.chatroomInfoId = chatroomInfoId;
    }
    
    public ChatMessageCollection toCollection() {
        return ChatMessageCollection.builder()
                .senderNo(senderNo)
                .receiverNo(receiverNo)
                .message(message)
                .chatroomInfoId(new ObjectId(chatroomInfoId))
                .build();
    }
}