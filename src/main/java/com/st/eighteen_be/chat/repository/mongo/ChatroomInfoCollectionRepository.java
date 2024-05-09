package com.st.eighteen_be.chat.repository.mongo;

import com.st.eighteen_be.chat.model.collection.ChatroomInfoCollection;
import com.st.eighteen_be.chat.model.dto.response.ChatroomWithLastestMessageDTO;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

/**
 * packageName    : com.st.eighteen_be.chat.repository
 * fileName       : ChatroomInfoCollectionRepository
 * author         : ipeac
 * date           : 24. 4. 5.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 4. 5.        ipeac       최초 생성
 */
public interface ChatroomInfoCollectionRepository extends MongoRepository<ChatroomInfoCollection, String>, CustomChatRepository {
    Optional<ChatroomInfoCollection> findBySenderNoAndReceiverNo(Long senderNo, Long receiverNo);

    @Aggregation(pipeline = {
            "{ $match: { 'senderNo': ?0 } }",
            "{ $lookup: { from: 'CHAT_MESSAGE', localField: '_id', foreignField: 'chatroomInfoId', as: 'latestMessage' } }",
            "{ $unwind: { path: '$latestMessage', preserveNullAndEmptyArrays: true } }",
            "{ $sort: { 'latestMessage.createdAt': -1 } }",
            "{ $group: { _id: $_id, senderNo: { $first: '$senderNo' }, receiverNo: { $first: '$receiverNo' }, chatroomType: { $first: '$chatroomType' }, createdAt: { $first: '$createdAt' }, updatedAt: { $first: '$updatedAt' }, lastestMessage: { $first: '$latestMessage' } } }",
            "{ $project: { senderNo: 1, receiverNo: 1, chatroomType: 1, createdAt: 1, updatedAt: 1, 'lastestMessage.message': 1, 'lastestMessage.createdAt': 1 } }"
    })
    List<ChatroomWithLastestMessageDTO> findAllChatroomBySenderNo(Long senderNo);
}