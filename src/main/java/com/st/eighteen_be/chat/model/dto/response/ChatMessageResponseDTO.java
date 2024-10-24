package com.st.eighteen_be.chat.model.dto.response;

import com.st.eighteen_be.chat.model.collection.ChatMessageCollection;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link ChatMessageCollection}
 */
@Schema(description = "채팅 메시지 응답 DTO")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessageResponseDTO implements Serializable {
    
    @Schema(description = "발신자 아이디", example = "senderUniqueId")
    @NotNull
    private String senderId;
    
    @Schema(description = "수신자 아이디", example = "receiverUniqueId")
    @NotNull
    private String receiverId;
    
    @Schema(description = "메시지", example = "안녕하세요")
    @NotNull
    @NotEmpty
    private String message;
    
    @Schema(description = "생성 시간", example = "2021-04-12T00:00:00")
    @FutureOrPresent
    private LocalDateTime createdAt;
    
    @Schema(description = "수정 시간", example = "2021-04-12T00:00:00")
    @FutureOrPresent
    private LocalDateTime updatedAt;
    
    @Builder
    private ChatMessageResponseDTO(String senderId, String receiverId, String message, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.message = message;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
