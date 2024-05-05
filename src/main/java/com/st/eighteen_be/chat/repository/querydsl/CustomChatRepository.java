package com.st.eighteen_be.chat.repository.querydsl;

import com.st.eighteen_be.chat.model.collection.ChatroomInfoCollection;

import java.util.List;

/**
 * packageName    : com.st.eighteen_be.chat.repository.qdsl
 * fileName       : CustomeChatRepository
 * author         : ipeac
 * date           : 24. 5. 6.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 5. 6.        ipeac       최초 생성
 */
public interface CustomChatRepository {
    List<ChatroomInfoCollection> findAllChatroomBySenderNo(Long senderNo);
}