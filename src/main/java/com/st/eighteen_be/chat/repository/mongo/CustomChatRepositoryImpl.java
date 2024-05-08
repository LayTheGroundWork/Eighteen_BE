package com.st.eighteen_be.chat.repository.mongo;

import com.st.eighteen_be.chat.model.dto.response.ChatroomWithLastestMessageDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
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
    public List<ChatroomWithLastestMessageDTO> findAllChatroomBySenderNo(Long senderNo) {
        MatchOperation matchStage = Aggregation.match(Criteria.where("senderNo").is(senderNo));
        
        LookupOperation lookup = LookupOperation.newLookup()
                .from("CHAT_MESSAGE")
                .let(VariableOperators.Let.ExpressionVariable.newVariable("chatroomInfoId").forField("$_id"))
                .pipeline(
                        Aggregation.match(
                                new Criteria().andOperator(
                                        Criteria.where("chatroomInfoId").is("$chatroomInfoId")
                                )
                        ),
                        Aggregation.sort(Sort.Direction.DESC, "createdAt"),
                        Aggregation.limit(1),
                        Aggregation.project().andExclude("_id").andInclude("chatroomInfoId", "message", "createdAt")
                )
                .as("latestMessage");
        
        UnwindOperation unwindStage = Aggregation.unwind("latestMessage");
        
        ProjectionOperation projectStage = Aggregation.project()
                .andInclude("_id", "chatroomType", "receiverNo", "senderNo", "createdAt", "updatedAt")
                .and("latestMessage").as("latestMessage");
        
        Aggregation aggregation = Aggregation.newAggregation(matchStage, lookup, unwindStage, projectStage);
        
        AggregationResults<ChatroomWithLastestMessageDTO> results = mt.aggregate(aggregation, "CHATROOM_INFO", ChatroomWithLastestMessageDTO.class);
        
        return results.getMappedResults();
    }
}