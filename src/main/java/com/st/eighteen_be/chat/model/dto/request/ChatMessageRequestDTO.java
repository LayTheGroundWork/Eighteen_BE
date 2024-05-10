package com.st.eighteen_be.chat.model.dto.request;

import com.st.eighteen_be.chat.model.collection.ChatMessageCollection;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.bson.types.ObjectId;

@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessageRequestDTO {
    
    @NotNull(message = "senderNo can`t be null")
    private Long senderNo;
    
    @NotNull(message = "message can`t be null")
    @NotEmpty(message = "message can`t be empty")
    @NotBlank(message = "message can`t be blank")
    private String message;
    
    @NotNull(message = "receiverNo can`t be null")
    private Long receiverNo;
    
    private ObjectId chatroomInfoId;
    
    @Builder
    private ChatMessageRequestDTO(Long senderNo, String message, Long receiverNo, ObjectId chatroomInfoId) {
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
                .chatroomInfoId(chatroomInfoId)
                .build();
    }
}