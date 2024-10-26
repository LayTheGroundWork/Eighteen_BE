package com.st.eighteen_be.tournament.repository.redis_template;

import com.st.eighteen_be.tournament.domain.redishash.MostLikedUserRedisHash;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * packageName    : com.st.eighteen_be.tournament.repository.redis_template
 * fileName       : MostLikedUserRepositoryCustom
 * author         : Jun
 * date           : 24. 10. 26.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 10. 26.        Jun       최초 생성
 */
@Repository
public interface MostLikedUserRepositoryCustom {
    List<MostLikedUserRedisHash> findAllByCategory(String category);
}
