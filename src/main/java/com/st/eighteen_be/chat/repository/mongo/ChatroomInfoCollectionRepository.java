package com.st.eighteen_be.chat.repository.mongo;

import com.st.eighteen_be.chat.model.collection.ChatroomInfoCollection;
import org.springframework.data.mongodb.repository.MongoRepository;

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
    Optional<ChatroomInfoCollection> findByRoomId(String roomId);
    
    Optional<ChatroomInfoCollection> findByPostNoAndMemberNo(Long postNo, Long memberNo);
}