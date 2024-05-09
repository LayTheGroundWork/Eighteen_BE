package com.st.eighteen_be.chat.model.dto.response;

import com.st.eighteen_be.chat.model.collection.ChatMessageCollection;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link ChatMessageCollection}
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessageResponseDTO implements Serializable {
    @NotNull
    private Long senderNo;
    @NotNull
    private Long receiverNo;
    @NotNull
    @NotEmpty
    private String message;
    @FutureOrPresent
    private LocalDateTime createdAt;
    @FutureOrPresent
    private LocalDateTime updatedAt;
    
    @Builder
    private ChatMessageResponseDTO(Long senderNo, Long receiverNo, String message, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.senderNo = senderNo;
        this.receiverNo = receiverNo;
        this.message = message;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}