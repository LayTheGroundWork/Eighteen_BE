package com.st.eighteen_be.chat.model.entity;

import com.st.eighteen_be.chat.model.vo.ChatroomInfo;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "CHAT_MESSAGE")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ChatMessageCollection {
    @Id
    @Field(name = "ID")
    private Long id;
    
    //TODO 회원 존재하지 않기에 임시 String 처리
    @Field(value = "SENDER")
    private String sender;
    
    //TODO 회원 존재하지 않기에 임시 String 처리
    @Field(value = "RECEIVER")
    private String receiver;
    
    @Field(value = "MESSAGE")
    private String message;
    
    private ChatroomInfo chatroomInfo;
    
    @Builder
    private ChatMessageCollection(Long id, String sender, String receiver, String message, ChatroomInfo chatroomInfo) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.chatroomInfo = chatroomInfo;
    }
}