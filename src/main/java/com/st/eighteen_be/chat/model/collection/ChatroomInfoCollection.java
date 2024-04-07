package com.st.eighteen_be.chat.model.collection;

import com.st.eighteen_be.chat.model.vo.ChatroomType;
import com.st.eighteen_be.common.converter.ChatroomConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;

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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ChatroomInfoCollection {
    @Id
    @Field(name = "ROOM_ID")
    private String roomId;
    
    @Field(name = "CHATROOM_NAME")
    private String chatroomName;
    
    @Convert(converter = ChatroomConverter.class)
    @Column(name = "CHATROOM_TYPE", nullable = false)
    private ChatroomType chatroomType;
    
    @DBRef(lazy = true)
    private List<ChatMessageCollection> chatMessageCollections = new ArrayList<>();
    
    @Builder
    private ChatroomInfoCollection(String roomId, String chatroomName, ChatroomType chatroomType, List<ChatMessageCollection> chatMessageCollections) {
        this.roomId = roomId;
        this.chatroomName = chatroomName;
        this.chatroomType = chatroomType;
        this.chatMessageCollections = new ArrayList<>();
    }
    
    public static ChatroomInfoCollection of(String roomId, String chatroomName, ChatroomType chatroomType) {
        return ChatroomInfoCollection.builder()
                .roomId(roomId)
                .chatroomName(chatroomName)
                .chatroomType(chatroomType)
                .build();
    }
    
    public void addChatMessage(ChatMessageCollection chatMessage) {
        this.chatMessageCollections.add(chatMessage);
    }
}