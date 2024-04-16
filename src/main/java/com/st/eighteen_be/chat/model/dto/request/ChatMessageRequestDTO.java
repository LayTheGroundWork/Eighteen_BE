package com.st.eighteen_be.chat.model.dto.request;

import com.st.eighteen_be.chat.model.collection.ChatMessageCollection;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

public record ChatMessageRequestDTO(
        @NotNull
        String roomId,
        
        @NotNull(message = "sender can`t be null")
        @NotEmpty(message = "sender can`t be empty")
        @NotBlank(message = "sender can`t be blank")
        String sender,
        
        @NotNull(message = "message can`t be null")
        @NotEmpty(message = "message can`t be empty")
        @NotBlank(message = "message can`t be blank")
        String message,
        
        @NotNull(message = "receiver can`t be null")
        @NotEmpty(message = "receiver can`t be empty")
        @NotBlank(message = "receiver can`t be blank")
        String receiver
) {
    @Builder
    public ChatMessageRequestDTO {
    }
    
    public ChatMessageCollection toCollection() {
        return ChatMessageCollection.builder()
                .roomId(roomId())
                .sender(sender())
                .receiver(receiver())
                .message(message())
                .build();
    }
}