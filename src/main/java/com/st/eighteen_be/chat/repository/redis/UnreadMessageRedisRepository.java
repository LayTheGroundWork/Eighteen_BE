package com.st.eighteen_be.chat.repository.redis;

import com.st.eighteen_be.chat.model.redishash.UnreadMessageCount;
import org.springframework.data.repository.CrudRepository;

/**
 * packageName    : com.st.eighteen_be.chat.repository.redis
 * fileName       : UnreadMessageRedisRepository
 * author         : ipeac
 * date           : 24. 5. 11.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 5. 11.        ipeac       최초 생성
 */
public interface UnreadMessageRedisRepository extends CrudRepository<UnreadMessageCount, String> {

}