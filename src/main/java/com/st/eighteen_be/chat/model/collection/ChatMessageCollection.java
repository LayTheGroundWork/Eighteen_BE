package com.st.eighteen_be.chat.model.collection;

import com.st.eighteen_be.chat.model.dto.response.ChatMessageResponseDTO;
import com.st.eighteen_be.common.basetime.BaseDocument;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "CHAT_MESSAGE")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@SuperBuilder //TODO 이걸 사용할 수 밖에 없는건가?.. 생성자에 @Builder 를 사용하고싶은데
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
    
    private ChatMessageCollection(LocalDateTime createdAt, LocalDateTime updatedAt, String id, String roomId, Long sender, Long receiver, String message) {
        super(createdAt, updatedAt);
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
    
    public ChatMessageResponseDTO toResponseDTO() {
        
        return ChatMessageResponseDTO.builder()
                .id(getId())
                .roomId(getRoomId())
                .sender(getSender())
                .receiver(getReceiver())
                .message(getMessage())
                .createdAt(getCreatedAt())
                .updatedAt(getUpdatedAt())
                .build();
    }
    
}