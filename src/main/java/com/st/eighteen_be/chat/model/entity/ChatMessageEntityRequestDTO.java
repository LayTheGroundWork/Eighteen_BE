package com.st.eighteen_be.chat.model.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record ChatMessageEntityRequestDTO(
        @NotNull(message = "sender can`t be null")
        @NotEmpty(message = "sender can`t be empty")
        @NotBlank(message = "sender can`t be blank")
        String sender,
        
        @NotNull(message = "message can`t be null")
        @NotEmpty(message = "message can`t be empty")
        @NotBlank(message = "message can`t be blank")
        String message
) {}