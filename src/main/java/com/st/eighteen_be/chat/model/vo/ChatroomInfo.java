package com.st.eighteen_be.chat.model.vo;

import com.st.eighteen_be.common.converter.ChatroomConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

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
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatroomInfo {
    @Field(name = "CHATROOM_ID")
    private Long roomId;
    
    @Field(name = "CHATROOM_NAME")
    private String chatroomName;
    
    @Convert(converter = ChatroomConverter.class)
    @Column(name = "CHATROOM_TYPE", nullable = false)
    private ChatroomType chatroomType;
    
    @Builder
    private ChatroomInfo(Long roomId, String chatroomName, ChatroomType chatroomType) {
        this.roomId = roomId;
        this.chatroomName = chatroomName;
        this.chatroomType = chatroomType;
    }
}