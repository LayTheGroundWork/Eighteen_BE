package com.st.eighteen_be.chat.model.entity;

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
    
    @Column(name = "CHATROOM_ID", nullable = false)
    private Long chatroomId;
    
    @Column(name = "CHATROOM_NAME", nullable = false)
    private String chatroomName;
    
    @Column(name = "CHATROOM_TYPE", nullable = false)
    private ChatroomType chatroomType;
    
    @Builder
    private ChatMessageEntity(Long id, String sender) {
        this.id = id;
        this.sender = sender;
    }
}