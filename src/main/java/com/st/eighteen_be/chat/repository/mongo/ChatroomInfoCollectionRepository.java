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
public interface ChatroomInfoCollectionRepository extends MongoRepository<ChatroomInfoCollection, String> {
    Optional<ChatroomInfoCollection> findBySenderNoAndReceiverNo(Long senderNo, Long receiverNo);

    /**
     * FIXME : 자바 쿼리로 변경이 가능하다면 변경하는 것이 좋을 것 같다.
     * 발신자 번호로 채팅방 목록 조회
     *
     * @param senderNo 채팅 송신자 번호
     * @return 채팅방 목록
     */
    @Aggregation(pipeline = {
            """
                    { $match: { 'senderNo': ?0 } }
                    """,
            """
                    { $lookup: {
                        from: 'chat_message',
                        let: { 'chatroomInfoId': '$_id' },
                        pipeline: [
                            { $match: { $expr: { $eq: ['$chatroomInfoId', '$$chatroomInfoId'] } } },
                            { $sort: { 'createdAt': -1 } },
                            { $limit: 1 },
                            { $project: { 'message': 1, 'createdAt': 1 } }
                        ],
                        as: 'latestMessage'
                    } }
                    """,
            """
                    { $unwind: {
                        path: '$latestMessage',
                        preserveNullAndEmptyArrays: true
                    } }
                    """,
            """
                    { $project: {
                        '_id': 1,
                        'senderNo': 1,
                        'receiverNo': 1,
                        'chatroomType': 1,
                        'createdAt': 1,
                        'updatedAt': 1,
                        'message': '$latestMessage.message',
                        'messageCreatedAt': '$latestMessage.createdAt'
                    } }
                    """
    })
    List<ChatroomWithLastestMessageDTO> findAllChatroomBySenderNo(Long senderNo);
}