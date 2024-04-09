package com.st.eighteen_be.chat.service;

import com.st.eighteen_be.chat.model.dto.request.ChatMessageRequestDTO;

public interface ChatMessageService {
    void processMessage(ChatMessageRequestDTO messageDto);
}