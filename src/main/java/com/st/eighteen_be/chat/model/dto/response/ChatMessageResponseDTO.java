package com.st.eighteen_be.chat.model.dto.response;

import com.st.eighteen_be.chat.model.collection.ChatMessageCollection;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.bson.types.ObjectId;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link ChatMessageCollection}
 */
public record ChatMessageResponseDTO(
        ObjectId id,
        @NotNull Long senderNo,
        @NotNull Long receiverNo,
        @NotNull @NotEmpty String message,
        @FutureOrPresent LocalDateTime createdAt,
        @FutureOrPresent LocalDateTime updatedAt
) implements Serializable {
    @Builder
    public ChatMessageResponseDTO {
    }
}