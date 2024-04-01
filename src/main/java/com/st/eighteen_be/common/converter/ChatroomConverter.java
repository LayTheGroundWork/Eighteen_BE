package com.st.eighteen_be.common.converter;

import com.st.eighteen_be.chat.model.vo.ChatroomType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * packageName    : com.st.eighteen_be.common.converter
 * fileName       : ChatroomConverter
 * author         : ipeac
 * date           : 2024-04-01
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-04-01        ipeac       최초 생성
 */
@Converter
public class ChatroomConverter implements AttributeConverter<ChatroomType, String> {
    @Override
    public String convertToDatabaseColumn(ChatroomType chatroomType) {
        if (chatroomType == null) {
            return null;
        }
        
        return chatroomType.getCode();
    }
    
    @Override
    public ChatroomType convertToEntityAttribute(String code) {
        if (code == null) {
            return null;
        }
        
        return ChatroomType.findBy(code);
    }
}