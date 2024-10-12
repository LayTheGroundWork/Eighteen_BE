package com.st.eighteen_be.chat.service.helper;

import com.st.eighteen_be.common.exception.sub_exceptions.data_exceptions.BadRequestException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * packageName    : com.st.eighteen_be.chat.service.helper
 * fileName       : ChatUserHelperTest
 * author         : ipeac
 * date           : 24. 4. 29.
 * description    : Unit tests for ChatUserHelper class
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 4. 29.        ipeac       최초 생성
 */
@DisplayName("ChatUserHelper 테스트")
public class ChatUserHelperTest {
    
    @Test
    @DisplayName("validNotSameUser 테스트 - 두 유저가 다를 때")
    void testValidNotSameUserWhenNotSameThenNoException() {
        // given
        String senderId = "senderIdTester";
        String receiverId = "receiverIdTester";
        
        // when & then
        assertDoesNotThrow(() -> ChatUserHelper.validNotSameUser(senderId, receiverId));
    }
    
    @Test
    @DisplayName("validNotSameUser 테스트 - 두 유저가 같을 때")
    void testValidNotSameUserWhenSameThenBadRequestException() {
        // Arrange
        String senderId = "senderIdTester";
        String receiverId = "senderIdTester";
        
        // when & then
        Assertions.assertThatThrownBy(() -> ChatUserHelper.validNotSameUser(senderId, receiverId))
                .isInstanceOf(BadRequestException.class);
    }
}
