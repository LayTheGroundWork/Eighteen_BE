package com.st.eighteen_be.tournament.repository.redis_template;

import com.st.eighteen_be.tournament.domain.redishash.MostLikedUserRedisHash;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

import static com.st.eighteen_be.tournament.service.TournamentService.MOST_LIKED_USER_KEY;

/**
 * packageName    : com.st.eighteen_be.tournament.repository.redis_template
 * fileName       : MostLikedUserRepositoryCustomImpl
 * author         : Jun
 * date           : 24. 10. 26.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 10. 26.        Jun       최초 생성
 */
@Repository
@RequiredArgsConstructor
public class MostLikedUserRepositoryCustomImpl implements MostLikedUserRepositoryCustom {
    private final RedisTemplate<String, MostLikedUserRedisHash> redisTemplate;
    
    public List<MostLikedUserRedisHash> findAllByCategory(String category) {
        String categoryKey = String.format(MOST_LIKED_USER_KEY + ":%s", category);
        
        return redisTemplate.opsForHash().values(categoryKey).stream()
                       .map(obj -> (MostLikedUserRedisHash) obj)
                       .collect(Collectors.toList());
    }
}
