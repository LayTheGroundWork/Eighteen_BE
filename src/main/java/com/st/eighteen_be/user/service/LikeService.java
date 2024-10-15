package com.st.eighteen_be.user.service;

import com.st.eighteen_be.user.domain.UserInfo;
import com.st.eighteen_be.user.domain.UserLike;
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
    private final UserService userService;

    public static final String LIKE_COUNT_PREFIX = "likeCount:";
    public static final String USER_LIKES_PREFIX = "userLikes:";


    public void addLike(String uniqueId, Integer likedId){

        String userLikesKey = USER_LIKES_PREFIX + uniqueId;

        if (Boolean.TRUE.equals(redisLikeTemplate.opsForSet().isMember(userLikesKey, likedId.toString()))) {
            throw new IllegalStateException("Already liked");
        }

        redisLikeTemplate.opsForSet().add(userLikesKey, likedId.toString());
        redisLikeTemplate.opsForValue().increment(LIKE_COUNT_PREFIX + likedId);
    }

    public void cancelLike(String uniqueId, Integer likedId){
        String userLikesKey = USER_LIKES_PREFIX + uniqueId;

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
            int likeCount = userService.findLikeCountById(userId);
            redisLikeTemplate.opsForValue().set(likeCountKey, String.valueOf(likeCount));
            return likeCount;
        }
        return Integer.parseInt(count);
    }

    @Transactional(readOnly = true)
    public Set<String> getLikedUserIds(String uniqueId) {
        String userLikesKey = USER_LIKES_PREFIX + uniqueId;

        return redisLikeTemplate.opsForSet().members(userLikesKey);
    }

    @Transactional(readOnly = true)
    public boolean getLikedUserId(String uniqueId, Integer likedId) {
        String userLikesKey = USER_LIKES_PREFIX + uniqueId;

        return Boolean.TRUE.equals(redisLikeTemplate.opsForSet().isMember(userLikesKey, likedId));
    }

    public void backupUserLikeDataToMySQL(){
        log.info("user likes backup start");

        Set<String> userLikesRedisKey = redisLikeTemplate.keys(LikeService.USER_LIKES_PREFIX + "*");

        if(userLikesRedisKey != null) {
            for (String redisUserId : userLikesRedisKey) {
                String uniqueId = redisUserId.split(":")[1];

                UserInfo userInfo = userService.findByUniqueId(uniqueId);

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

                UserInfo userInfo = userService.findById(userId);

                int likeCount = Integer.parseInt(Objects.requireNonNull(redisLikeTemplate.opsForValue().get(data)));
                userInfo.backupLikeCount(likeCount);
            }
        }

    }
}
