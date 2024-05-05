package com.st.eighteen_be.chat.repository.querydsl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;

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
@RequiredArgsConstructor
public class CustomChatRepositoryImpl implements CustomChatRepository {
    private final MongoTemplate mt;
    
    @Override
    public List<ChatRoomWithLatestMessageDTO> findAllChatroomBySenderNo(Long senderNo) {
        MatchOperation matchStage = Aggregation.match(Criteria.where("sender_no").is(senderNo));
        
        LookupOperation lookupStage = LookupOperation.newLookup()
                .from("chatMessageCollection")
                .localField("_id")
    }
}