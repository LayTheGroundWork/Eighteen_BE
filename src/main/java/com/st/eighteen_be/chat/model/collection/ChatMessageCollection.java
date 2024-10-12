package com.st.eighteen_be.chat.model.collection;

import com.st.eighteen_be.chat.model.dto.response.ChatMessageResponseDTO;
import com.st.eighteen_be.common.basetime.BaseDocument;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.time.LocalDateTime;

@Document(collection = "chat_message")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@SuperBuilder
public class ChatMessageCollection extends BaseDocument {
    @Id
    @Field(value = "_id", targetType = FieldType.OBJECT_ID)
    private ObjectId _id;
    
    @Field(value = "chatroomInfoId", targetType = FieldType.OBJECT_ID)
    private ObjectId chatroomInfoId;
    
    //TODO 회원 존재하지 않기에 임시 String 처리
    @Field(value = "senderId")
    private String senderId;
    
    //TODO 회원 존재하지 않기에 임시 String 처리
    @Field(value = "receiverId")
    private String receiverId;
    
    @Field(value = "message")
    private String message;
    
    private ChatMessageCollection(LocalDateTime createdAt, LocalDateTime updatedAt, ObjectId _id, ObjectId chatroomInfoId, String senderId, String receiverId, String message) {
        super(createdAt, updatedAt);
        
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.message = message;
        this._id = _id;
        this.chatroomInfoId = chatroomInfoId;
    }
    
    public ChatMessageResponseDTO toResponseDTO() {
        return ChatMessageResponseDTO.builder()
                .senderId(getSenderId())
                .receiverId(getReceiverId())
                .message(getMessage())
                .createdAt(getCreatedAt())
                .updatedAt(getUpdatedAt())
                .build();
    }
}
