package com.st.eighteen_be.chat.utility;

import lombok.extern.slf4j.Slf4j;

/**
 * packageName    : com.st.eighteen_be.chat.utility
 * fileName       : ChatUtilityMaker
 * author         : ipeac
 * date           : 2024-04-01
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-04-01        ipeac       최초 생성
 */
@Slf4j
public class ChatUtilityMaker {
    
    public static String createChatTopic(String chatRoomId) {
        log.info("ChatUtilityMaker.createChatroomId : {} ", chatRoomId);
        return "chatroom-" + chatRoomId;
    }
}