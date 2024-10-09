package com.st.eighteen_be.tournament.repository;

import com.st.eighteen_be.tournament.domain.redishash.RandomUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

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
public interface RandomUserRedisRepository extends CrudRepository<RandomUser, String> {

}
