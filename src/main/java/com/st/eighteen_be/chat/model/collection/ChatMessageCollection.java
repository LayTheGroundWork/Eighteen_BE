package com.st.eighteen_be.chat.model.collection;

import com.st.eighteen_be.chat.model.dto.response.ChatMessageResponseDTO;
import com.st.eighteen_be.common.basetime.BaseDocument;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.time.LocalDateTime;

@Document(collection = "CHAT_MESSAGE")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@SuperBuilder
public class ChatMessageCollection extends BaseDocument {
    @Id
    @Field(value = "_id", targetType = FieldType.OBJECT_ID)
    private String id;
    
    @Field(value = "CHATROOM_INFO_ID")
    private String chatroomInfoId;
    
    //TODO 회원 존재하지 않기에 임시 String 처리
    @Field(value = "SENDER_NO")
    private Long senderNo;
    
    //TODO 회원 존재하지 않기에 임시 String 처리
    @Field(value = "RECEIVER_NO")
    private Long receiverNo;
    
    @Field(value = "MESSAGE")
    private String message;
    
    private ChatMessageCollection(LocalDateTime createdAt, LocalDateTime updatedAt, String id, String chatroomInfoId, Long senderNo, Long receiverNo, String message) {
        super(createdAt, updatedAt);
        this.id = id;
        this.chatroomInfoId = chatroomInfoId;
        this.senderNo = senderNo;
        this.receiverNo = receiverNo;
        this.message = message;
    }
    
    public static ChatMessageCollection of(String id, Long senderNo, Long receiverNo, String message) {
        return ChatMessageCollection.builder()
                .id(id)
                .senderNo(senderNo)
                .receiverNo(receiverNo)
                .message(message)
                .build();
    }
    
    public ChatMessageResponseDTO toResponseDTO() {
        return ChatMessageResponseDTO.builder()
                .id(getId())
                .senderNo(getSenderNo())
                .receiverNo(getReceiverNo())
                .message(getMessage())
                .createdAt(getCreatedAt())
                .updatedAt(getUpdatedAt())
                .build();
    }
}