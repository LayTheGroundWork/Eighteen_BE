package com.st.eighteen_be.chat.repository.mongo;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

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
@Repository
@RequiredArgsConstructor
public class CustomChatRepositoryImpl implements CustomChatRepository {
    private final MongoTemplate mt;

  /*  @Override
    public List<ChatroomWithLastestMessageDTO> findAllChatroomBySenderNo(Long senderNo) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("senderNo").is(senderNo)),
                Aggregation.lookup("CHAT_MESSAGE", "_id", "chatroomInfoId", "latestMessage"),
                Aggregation.unwind("latestMessage"),
                Aggregation.project()
                        .andInclude("senderNo", "receiverNo", "chatroomType", "createdAt", "updatedAt")
                        .and("latestMessage.message").as("lastestMessage.message")
                        .and("latestMessage.createdAt").as("lastestMessage.createdAt")
        );

        AggregationResults<ChatroomWithLastestMessageDTO> results = mt.aggregate(
                aggregation, "CHATROOM_INFO", ChatroomWithLastestMessageDTO.class
        );

        return results.getMappedResults();
    }*/
}