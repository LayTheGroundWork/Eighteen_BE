package com.st.eighteen_be.chat.model.collection;

import com.st.eighteen_be.common.basetime.BaseDocument;
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
public class ChatMessageCollection extends BaseDocument {
    @Id
    private String id;
    
    @Field(value = "ROOM_ID")
    private String roomId;
    
    //TODO 회원 존재하지 않기에 임시 String 처리
    @Field(value = "SENDER")
    private Long sender;
    
    //TODO 회원 존재하지 않기에 임시 String 처리
    @Field(value = "RECEIVER")
    private Long receiver;
    
    @Field(value = "MESSAGE")
    private String message;
    
    @Builder
    private ChatMessageCollection(String id, String roomId, Long sender, long receiver, String message) {
        this.id = id;
        this.roomId = roomId;
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
    }
    
    public static ChatMessageCollection of(String id, String roomId, Long sender, long receiver, String message) {
        return ChatMessageCollection.builder()
                .id(id)
                .roomId(roomId)
                .sender(sender)
                .receiver(receiver)
                .message(message)
                .build();
    }
}