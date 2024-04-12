package com.st.eighteen_be.chat.model.dto.request;

import com.st.eighteen_be.chat.model.collection.ChatMessageCollection;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

public record ChatMessageRequestDTO(
        @NotNull
        String roomId,
        
        @NotNull(message = "senderNo can`t be null")
        Long senderNo,
        
        @NotNull(message = "message can`t be null")
        @NotEmpty(message = "message can`t be empty")
        @NotBlank(message = "message can`t be blank")
        String message,
        
        @NotNull(message = "receiverNo can`t be null")
        Long receiverNo
) {
    @Builder
    public ChatMessageRequestDTO {
    }
    
    public ChatMessageCollection toCollection() {
        return ChatMessageCollection.builder()
                .roomId(roomId())
                .sender(senderNo())
                .receiver(receiverNo())
                .message(message())
                .build();
    }
}