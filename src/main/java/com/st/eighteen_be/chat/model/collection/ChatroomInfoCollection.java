package com.st.eighteen_be.chat.model.collection;

import com.st.eighteen_be.chat.model.vo.ChatroomType;
import com.st.eighteen_be.common.converter.ChatroomConverter;
import com.st.eighteen_be.common.exception.ErrorCode;
import com.st.eighteen_be.common.exception.sub_exceptions.data_exceptions.BadRequestException;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

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
@Document(collection = "CHATROOM_INFO")
@CompoundIndexes({
        @CompoundIndex(name = "chatroom_info_idx", def = "{'POST_NO': 1, 'USER_NO': 1}")
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ChatroomInfoCollection {
    @Indexed(unique = true)
    @Field(name = "ROOM_ID")
    private String roomId;
    
    @Field(name = "POST_NO")
    private Long postNo;
    
    @Field(name = "USER_NO")
    private Long memberNo;
    
    @Convert(converter = ChatroomConverter.class)
    @Column(name = "CHATROOM_TYPE", nullable = false)
    private ChatroomType chatroomType;
    
    @Builder
    private ChatroomInfoCollection(String roomId, Long postNo, Long memberNo, ChatroomType chatroomType) {
        this.roomId = roomId;
        this.postNo = postNo;
        this.memberNo = memberNo;
        this.chatroomType = chatroomType;
    }
    
    public static ChatroomInfoCollection of(Long postNo, Long memberNo, ChatroomType chatroomType) {
        if (Objects.isNull(postNo) || Objects.isNull(memberNo) || Objects.isNull(chatroomType)) {
            throw new BadRequestException(ErrorCode.NOT_NULL);
        }
        
        return ChatroomInfoCollection.builder()
                .postNo(postNo)
                .memberNo(memberNo)
                .chatroomType(chatroomType)
                .build();
    }
}