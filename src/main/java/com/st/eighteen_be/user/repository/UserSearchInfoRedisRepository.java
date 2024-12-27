package com.st.eighteen_be.user.repository;

import com.st.eighteen_be.user.redishash.UserSearchInfoHash;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * packageName    : com.st.eighteen_be.user.repository
 * fileName       : UserRedisRepository
 * author         : sjunpark
 * date           : 24. 12. 24.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 12. 24.        sjunpark       최초 생성
 */
@Repository
public interface UserSearchInfoRedisRepository extends CrudRepository<UserSearchInfoHash, String> {
    @NotNull List<UserSearchInfoHash> findAll();
}
