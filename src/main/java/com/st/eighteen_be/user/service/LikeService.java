package com.st.eighteen_be.user.service;

import com.st.eighteen_be.common.exception.ErrorCode;
import com.st.eighteen_be.common.exception.sub_exceptions.data_exceptions.NotFoundException;
import com.st.eighteen_be.common.exception.sub_exceptions.data_exceptions.NotValidException;
import com.st.eighteen_be.jwt.JwtTokenProvider;
import com.st.eighteen_be.user.domain.UserInfo;
import com.st.eighteen_be.user.domain.UserLike;
import com.st.eighteen_be.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Set;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class LikeService {

    private final RedisTemplate<String,String> redisLikeTemplate;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    public static final String LIKE_COUNT_PREFIX = "likeCount:";
    public static final String USER_LIKES_PREFIX = "userLikes:";


    public void addLike(String accessToken, Integer likedId){
        String likerId = getUserUniqueIdFromRequest(accessToken);

        if(userRepository.findById(likedId).isEmpty()){
            throw new NotFoundException(ErrorCode.NOT_FOUND_USER);
        }

        String userLikesKey = USER_LIKES_PREFIX + likerId;

        if (Boolean.TRUE.equals(redisLikeTemplate.opsForSet().isMember(userLikesKey, likedId.toString()))) {
            throw new IllegalStateException("Already liked");
        }

        redisLikeTemplate.opsForSet().add(userLikesKey, likedId.toString());
        redisLikeTemplate.opsForValue().increment(LIKE_COUNT_PREFIX + likedId);
    }

    public void cancelLike(String accessToken, Integer likedId){
        String likerId = getUserUniqueIdFromRequest(accessToken);
        String userLikesKey = USER_LIKES_PREFIX + likerId;

        if (Boolean.FALSE.equals(redisLikeTemplate.opsForSet().isMember(userLikesKey, likedId.toString()))) {
            //TODO: 레디스에 없지만 DB에는 있는지 확인하는 로직이 필요함
            throw new IllegalStateException("Not liked yet");
        }
        redisLikeTemplate.opsForSet().remove(userLikesKey,likedId);
        redisLikeTemplate.opsForValue().decrement(LIKE_COUNT_PREFIX + likedId);
    }

    @Transactional(readOnly = true)
    public int countLikes(Integer userId) {
        String likeCountKey = LIKE_COUNT_PREFIX + userId;
        String count = redisLikeTemplate.opsForValue().get(likeCountKey);
        if (count == null) {
            // Redis에 값이 없으면 데이터베이스에서 가져와 Redis에 저장
            int likeCount = userRepository.findLikeCountById(userId);
            redisLikeTemplate.opsForValue().set(likeCountKey, String.valueOf(likeCount));
            return likeCount;
        }
        return Integer.parseInt(count);
    }

    @Transactional(readOnly = true)
    public Set<String> getLikedUserIds(String accessToken) {
        String likerId = getUserUniqueIdFromRequest(accessToken);
        String userLikesKey = USER_LIKES_PREFIX + likerId;

        return redisLikeTemplate.opsForSet().members(userLikesKey);
    }

    @Transactional(readOnly = true)
    public boolean getLikedUserId(String accessToken, Integer likedId) {
        String likerId = getUserUniqueIdFromRequest(accessToken);
        String userLikesKey = USER_LIKES_PREFIX + likerId;

        return Boolean.TRUE.equals(redisLikeTemplate.opsForSet().isMember(userLikesKey, likedId));
    }


    private String getUserUniqueIdFromRequest(String accessToken){
        String requestAccessToken = jwtTokenProvider.resolveAccessToken(accessToken);
        if (requestAccessToken == null || !jwtTokenProvider.validateToken(requestAccessToken)) {
            throw new NotValidException(ErrorCode.ACCESS_TOKEN_NOT_VALID);
        }
        return jwtTokenProvider.getAuthentication(requestAccessToken).getName();
    }

    public void backupUserLikeDataToMySQL(){
        log.info("user likes backup start");

        Set<String> userLikesRedisKey = redisLikeTemplate.keys(LikeService.USER_LIKES_PREFIX + "*");

        if(userLikesRedisKey != null) {
            for (String redisUserId : userLikesRedisKey) {
                String uniqueId = redisUserId.split(":")[1];

                UserInfo userInfo = userRepository.findByUniqueId(uniqueId).orElseThrow(
                        () -> new NotFoundException(ErrorCode.NOT_FOUND_USER)
                );

                Set<String> likedUser = Objects.requireNonNull(redisLikeTemplate.opsForSet().members(redisUserId));

                for (String str : likedUser) {
                    Integer likedId = Integer.parseInt(str);
                    UserLike.addLikedId(userInfo, likedId);
                }
            }
        }

    }

    public void backupLikeCountToMySQL(){
        log.info("user like count backup start");

        Set<String> likeCountRedisKey = redisLikeTemplate.keys(LikeService.LIKE_COUNT_PREFIX + "*");

        if(likeCountRedisKey != null) {
            for (String data : likeCountRedisKey) {
                Integer userId = Integer.parseInt(data.split(":")[1]);

                if (redisLikeTemplate.opsForValue().get(data) == null) break;

                UserInfo userInfo = userRepository.findById(userId).orElseThrow(
                        () -> new NotFoundException(ErrorCode.NOT_FOUND_USER)
                );

                int likeCount = Integer.parseInt(Objects.requireNonNull(redisLikeTemplate.opsForValue().get(data)));
                userInfo.backupLikeCount(likeCount);
            }
        }

    }
}
