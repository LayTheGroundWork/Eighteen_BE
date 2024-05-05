package com.st.eighteen_be.chat.model.collection;

import com.st.eighteen_be.chat.model.dto.response.ChatMessageResponseDTO;
import com.st.eighteen_be.common.basetime.BaseDocument;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.time.LocalDateTime;
import java.util.Objects;

@Document(collection = "CHAT_MESSAGE")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@SuperBuilder
public class ChatMessageCollection extends BaseDocument {
    @Id
    @Field(value = "_id", targetType = FieldType.OBJECT_ID)
    private String id;
    
    //TODO 회원 존재하지 않기에 임시 String 처리
    @Field(value = "SENDER")
    private Long sender;
    
    //TODO 회원 존재하지 않기에 임시 String 처리
    @Field(value = "RECEIVER")
    private Long receiver;
    
    @Field(value = "MESSAGE")
    private String message;
    
    
    private ChatMessageCollection(LocalDateTime createdAt, LocalDateTime updatedAt, String id, Long sender, Long receiver, String message) {
        super(createdAt, updatedAt);
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
    }
    
    public static ChatMessageCollection of(String id, Long sender, long receiver, String message) {
        return ChatMessageCollection.builder()
                .id(id)
                .sender(sender)
                .receiver(receiver)
                .message(message)
                .build();
    }
    
    public ChatMessageResponseDTO toResponseDTO() {
        
        return ChatMessageResponseDTO.builder()
                .id(getId())
                .sender(getSender())
                .receiver(getReceiver())
                .message(getMessage())
                .createdAt(getCreatedAt())
                .updatedAt(getUpdatedAt())
                .build();
    }
    
    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        ChatMessageCollection that = (ChatMessageCollection) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }
    
    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}