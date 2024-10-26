package com.st.eighteen_be.tournament.repository;

import com.st.eighteen_be.tournament.domain.redishash.MostLikedUserRedisHash;
import com.st.eighteen_be.tournament.repository.redis_template.MostLikedUserRepositoryCustom;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * packageName    : com.st.eighteen_be.tournament.repository
 * fileName       : RandomUserRedisRepository
 * author         : Jun
 * date           : 24. 10. 9.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 10. 9.        Jun       최초 생성
 */
@Repository
public interface MostLikedUserRepository extends CrudRepository<MostLikedUserRedisHash, String>, MostLikedUserRepositoryCustom {
    List<MostLikedUserRedisHash> findAllByCategory(String category);
}
