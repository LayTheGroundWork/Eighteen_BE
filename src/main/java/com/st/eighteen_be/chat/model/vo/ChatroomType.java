package com.st.eighteen_be.chat.model.vo;

import com.st.eighteen_be.common.exception.ErrorCode;
import com.st.eighteen_be.common.exception.sub_exceptions.data_exceptions.NotFoundException;
import lombok.Getter;

import java.util.Objects;

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
    PRIVATE("PRIVATE", "개인 채팅방"),
    GROUP("GROUP", "그룹 채팅방");
    
    private final String name;
    private final String code;
    private final String description;
    
    ChatroomType(String code, String description) {
        this.name = code;
        this.code = code;
        this.description = description;
    }
    
    public static ChatroomType findBy(String code) {
        for (ChatroomType type : values()) {
            if (Objects.equals(type.getCode(), code)) {
                return type;
            }
        }
        
        throw new NotFoundException(ErrorCode.NOT_FOUND_CHATROOM);
    }
}