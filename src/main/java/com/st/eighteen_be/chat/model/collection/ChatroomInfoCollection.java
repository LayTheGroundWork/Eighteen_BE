package com.st.eighteen_be.chat.model.collection;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.st.eighteen_be.chat.model.vo.ChatroomType;
import com.st.eighteen_be.common.basetime.BaseDocument;
import com.st.eighteen_be.common.converter.ChatroomTypeConverter;
import com.st.eighteen_be.common.exception.ErrorCode;
import com.st.eighteen_be.common.exception.sub_exceptions.data_exceptions.BadRequestException;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * packageName    : com.st.eighteen_be.chat.model.vo
 * fileName       : ChatroomInfo
 * author         : ipeac
 * date           : 2024-04-01
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-04-01        ipeac       최초 생성
 */
@Document(collection = "chatroom_info")
@CompoundIndexes({
        @CompoundIndex(name = "chatroom_info_idx", def = "{'senderId': 1, 'receiverId': 1}")
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@SuperBuilder
public class ChatroomInfoCollection extends BaseDocument {
    @Id
    @JsonSerialize(using = ToStringSerializer.class)
    @Field(value = "_id", targetType = FieldType.OBJECT_ID)
    private ObjectId _id;
    
    @Field(name = "senderId")
    private String senderId;
    
    @Field(name = "receiverId")
    private String receiverId;
    
    @Convert(converter = ChatroomTypeConverter.class)
    @Column(name = "chatroomType", nullable = false)
    private ChatroomType chatroomType;
    
    @Field(name = "leftUsers")
    @Builder.Default
    private Set<String> leftUsers = new HashSet<>();
    
    private ChatroomInfoCollection(LocalDateTime createdAt, LocalDateTime updatedAt, String senderId, String receiverId, ChatroomType chatroomType) {
        super(createdAt, updatedAt);
        
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.chatroomType = chatroomType;
    }
    
    public boolean isUserInChatroom(String userId) {
        return Objects.equals(senderId, userId) || Objects.equals(receiverId, userId);
    }
    
    public void addLeftUser(String userId) {
        leftUsers.add(userId);
    }
    
    public static ChatroomInfoCollection of(String senderId, String receiverId, ChatroomType chatroomType) {
        if (Objects.isNull(senderId) || Objects.isNull(receiverId) || Objects.isNull(chatroomType)) {
            throw new BadRequestException(ErrorCode.NOT_NULL);
        }
        
        return ChatroomInfoCollection.builder()
                       .senderId(senderId)
                       .receiverId(receiverId)
                       .chatroomType(chatroomType)
                       .build();
    }
}
