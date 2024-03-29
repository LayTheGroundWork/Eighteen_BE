package com.st.eighteen_be.chat.model.entity;

import lombok.Getter;

/**
 * packageName    : com.st.eighteen_be.chat.model.entity
 * fileName       : ChatroomType
 * author         : ipeac
 * date           : 2024-03-29
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-03-29        ipeac       최초 생성
 */
@Getter
public enum ChatroomType {
    PRIVATE("1", "개인 채팅방"),
    GROUP("2", "그룹 채팅방");
    
    private final String code;
    private final String description;
    
    ChatroomType(String code, String description) {
        this.code = code;
        this.description = description;
    }
}