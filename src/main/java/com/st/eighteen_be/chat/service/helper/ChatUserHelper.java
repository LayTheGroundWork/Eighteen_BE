package com.st.eighteen_be.chat.service.helper;

import com.st.eighteen_be.common.exception.ErrorCode;
import com.st.eighteen_be.common.exception.sub_exceptions.data_exceptions.BadRequestException;

import java.util.Objects;

/**
 * packageName    : com.st.eighteen_be.chat.service.helper
 * fileName       : ChatUserHelper
 * author         : ipeac
 * date           : 24. 4. 29.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 4. 29.        ipeac       최초 생성
 */
public final class ChatUserHelper {
    public static void validNotSameUser(Long senderNo, Long receiverNo) {
        if (Objects.equals(senderNo, receiverNo)) {
            throw new BadRequestException(ErrorCode.CHATROOM_SAME_USER);
        }
    }
}