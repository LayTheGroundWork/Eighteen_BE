package com.st.eighteen_be.chat.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record ChatMessageRequestDTO(
        @NotNull(message = "roomId can`t be null")
        Long roomId,
        
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
) {}