package com.st.eighteen_be.user.service;

import com.st.eighteen_be.common.exception.ErrorCode;
import com.st.eighteen_be.common.exception.sub_exceptions.data_exceptions.NotValidException;
import com.st.eighteen_be.jwt.JwtTokenProvider;
import com.st.eighteen_be.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class LikeService {

    private final RedisTemplate<String,String> redisLikeTemplate;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    private static final String LIKE_COUNT_PREFIX = "likeCount:";
    private static final String USER_LIKES_PREFIX = "userLikes:";


    @Transactional
    public void addLike(HttpServletRequest request, Integer likedId){
        String likerId = getUserUniqueIdFromRequest(request);
        String userLikesKey = USER_LIKES_PREFIX + likerId;

        if (Boolean.TRUE.equals(redisLikeTemplate.opsForSet().isMember(userLikesKey, likedId.toString()))) {
            //TODO: 레디스에 없지만 DB에는 있는지 확인하는 로직이 필요함
            throw new IllegalStateException("Already liked");
        }

        redisLikeTemplate.opsForSet().add(userLikesKey, likedId.toString());
        redisLikeTemplate.opsForValue().increment(LIKE_COUNT_PREFIX + likedId);
    }

    @Transactional
    public void cancelLike(HttpServletRequest request, Integer likedId){
        String likerId = getUserUniqueIdFromRequest(request);
        String userLikesKey = USER_LIKES_PREFIX + likerId;

        if (Boolean.FALSE.equals(redisLikeTemplate.opsForSet().isMember(userLikesKey, likedId.toString()))) {
            //TODO: 레디스에 없지만 DB에는 있는지 확인하는 로직이 필요함
            throw new IllegalStateException("Not liked yet");
        }
        redisLikeTemplate.opsForSet().remove(userLikesKey,likedId);
        redisLikeTemplate.opsForValue().decrement(LIKE_COUNT_PREFIX + likedId);
    }

    public int countLikes(Integer userId) {
        String likeCountKey = LIKE_COUNT_PREFIX + userId;
        String count = redisLikeTemplate.opsForValue().get(likeCountKey);
        if (count == null) {
            // Redis에 값이 없으면 데이터베이스에서 가져와 Redis에 저장
            int likeCount = userRepository.getLikeCount(userId);
            redisLikeTemplate.opsForValue().set(likeCountKey, String.valueOf(likeCount));
            return likeCount;
        }
        return Integer.parseInt(count);
    }

    public Set<String> getLikedUserIds(HttpServletRequest request) {
        String likerId = getUserUniqueIdFromRequest(request);
        String userLikesKey = USER_LIKES_PREFIX + likerId;

        return redisLikeTemplate.opsForSet().members(userLikesKey);
    }

    public boolean getLikedUserId(HttpServletRequest request, Integer likedId) {
        String likerId = getUserUniqueIdFromRequest(request);
        String userLikesKey = USER_LIKES_PREFIX + likerId;

        return Boolean.TRUE.equals(redisLikeTemplate.opsForSet().isMember(userLikesKey, likedId));
    }


    public String getUserUniqueIdFromRequest(HttpServletRequest request){
        String requestAccessToken = jwtTokenProvider.resolveAccessToken(request);
        if (requestAccessToken == null || !jwtTokenProvider.validateToken(requestAccessToken)) {
            throw new NotValidException(ErrorCode.ACCESS_TOKEN_NOT_VALID);
        }
        return jwtTokenProvider.getAuthentication(requestAccessToken).getName();
    }
}
