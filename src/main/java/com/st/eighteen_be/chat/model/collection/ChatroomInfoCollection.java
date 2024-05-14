package com.st.eighteen_be.chat.model.collection;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.st.eighteen_be.chat.model.vo.ChatroomType;
import com.st.eighteen_be.common.basetime.BaseDocument;
import com.st.eighteen_be.common.converter.ChatroomConverter;
import com.st.eighteen_be.common.exception.ErrorCode;
import com.st.eighteen_be.common.exception.sub_exceptions.data_exceptions.BadRequestException;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import lombok.AccessLevel;
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
import java.util.Objects;

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
        @CompoundIndex(name = "chatroom_info_idx", def = "{'SENDER_NO': 1, 'RECEIVER_NO': 1}")
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@SuperBuilder
public class ChatroomInfoCollection extends BaseDocument {
    @Id
    @JsonSerialize(using= ToStringSerializer.class)
    @Field(value = "_id", targetType = FieldType.OBJECT_ID)
    private ObjectId _id;
    
    @Field(name = "senderNo")
    private Long senderNo;
    
    @Field(name = "receiverNo")
    private Long receiverNo;
    
    @Convert(converter = ChatroomConverter.class)
    @Column(name = "chatroomType", nullable = false)
    private ChatroomType chatroomType;
    
    private ChatroomInfoCollection(LocalDateTime createdAt, LocalDateTime updatedAt, Long senderNo, Long receiverNo, ChatroomType chatroomType) {
        super(createdAt, updatedAt);
        this.senderNo = senderNo;
        this.receiverNo = receiverNo;
        this.chatroomType = chatroomType;
    }
    
    public static ChatroomInfoCollection of(Long senderNo, Long receiverNo, ChatroomType chatroomType) {
        if (Objects.isNull(senderNo) || Objects.isNull(receiverNo) || Objects.isNull(chatroomType)) {
            throw new BadRequestException(ErrorCode.NOT_NULL);
        }
        
        return ChatroomInfoCollection.builder()
                .senderNo(senderNo)
                .receiverNo(receiverNo)
                .chatroomType(chatroomType)
                .build();
    }
}