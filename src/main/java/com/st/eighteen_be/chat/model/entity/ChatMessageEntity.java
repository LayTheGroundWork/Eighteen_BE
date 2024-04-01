package com.st.eighteen_be.chat.model.entity;

import com.st.eighteen_be.chat.model.vo.ChatroomInfo;
import com.st.eighteen_be.common.basetime.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "CHAT_MESSAGE")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ChatMessageEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;
    
    //TODO 회원 존재하지 않기에 임시 String 처리
    @Column(name = "SENDER", nullable = false)
    private String sender;
    
    //TODO 회원 존재하지 않기에 임시 String 처리
    @Column(name = "RECEIVER", nullable = false)
    private String receiver;
    
    @Column(name = "MESSAGE", nullable = false)
    private String message;
    
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "roomId", column = @Column(name = "ROOM_ID", nullable = false)),
            @AttributeOverride(name = "chatroomName", column = @Column(name = "ROOM_NAME", nullable = false)),
            @AttributeOverride(name = "chatroomType", column = @Column(name = "ROOM_TYPE", nullable = false))
    })
    private ChatroomInfo chatroomInfo;
    
    @Builder
    private ChatMessageEntity(Long id, String sender, String receiver, String message, ChatroomInfo chatroomInfo) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.chatroomInfo = chatroomInfo;
    }
}