package com.st.eighteen_be.user.service;

import com.st.eighteen_be.user.domain.UserInfo;
import com.st.eighteen_be.user.domain.UserLike;
import java.util.Objects;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class LikeService {

    private final RedisTemplate<String,String> userLikesRedisTemplate;
    private final StringRedisTemplate likeCountRedisTemplate;
    private final UserService userService;

    public static final String LIKE_COUNT_PREFIX = "likeCount:";
    public static final String USER_LIKES_PREFIX = "userLikes:";


    public void addLike(String uniqueId, Integer userId){

        String userLikesKey = USER_LIKES_PREFIX + uniqueId;
        UserInfo user = userService.findById(userId);
        String likedId = Integer.toString(user.getId());

        if (Boolean.TRUE.equals(userLikesRedisTemplate.opsForSet().isMember(userLikesKey,likedId))) {
            throw new IllegalStateException("Already liked");
        }

        userLikesRedisTemplate.opsForSet().add(userLikesKey,likedId);
        likeCountRedisTemplate.opsForValue().increment(LIKE_COUNT_PREFIX + likedId);
    }

    public void cancelLike(String uniqueId, Integer userId){
        String userLikesKey = USER_LIKES_PREFIX + uniqueId;
        UserInfo user = userService.findById(userId);
        String likedId = Integer.toString(user.getId());

        if (Boolean.FALSE.equals(userLikesRedisTemplate.opsForSet().isMember(userLikesKey, likedId))) {
            //TODO: 레디스에 없지만 DB에는 있는지 확인하는 로직이 필요함
            throw new IllegalStateException("Not liked yet");
        }
        userLikesRedisTemplate.opsForSet().remove(userLikesKey,likedId);
        likeCountRedisTemplate.opsForValue().decrement(LIKE_COUNT_PREFIX + likedId);
    }

    @Transactional(readOnly = true)
    public int countLikes(Integer userId) {
        String likeCountKey = LIKE_COUNT_PREFIX + userId;
        String count = likeCountRedisTemplate.opsForValue().get((likeCountKey));

        if (count == null) {
            // Redis에 값이 없으면 데이터베이스에서 가져와 Redis에 저장
            String likeCount = Integer.toString(userService.findLikeCountById(userId));
            likeCountRedisTemplate.opsForValue().set(likeCountKey, likeCount);
            return Integer.parseInt(likeCount);
        }
        return Integer.parseInt(count);
    }

    @Transactional(readOnly = true)
    public Set<String> getLikedUserIds(String uniqueId) {
        String userLikesKey = USER_LIKES_PREFIX + uniqueId;

        return userLikesRedisTemplate.opsForSet().members(userLikesKey);
    }

    @Transactional(readOnly = true)
    public boolean getLikedUserId(String uniqueId, Integer userId) {
        String userLikesKey = USER_LIKES_PREFIX + uniqueId;
        UserInfo user = userService.findById(userId);
        String likedId = Integer.toString(user.getId());

        return Boolean.TRUE.equals(userLikesRedisTemplate.opsForSet().isMember(userLikesKey, likedId));
    }

    public void backupUserLikeDataToMySQL(){
        log.info("user likes backup start");

        Set<String> userLikesRedisKey = userLikesRedisTemplate.keys(LikeService.USER_LIKES_PREFIX + "*");

        if(userLikesRedisKey != null) {
            for (String redisUserId : userLikesRedisKey) {
                String uniqueId = redisUserId.split(":")[1];

                UserInfo userInfo = userService.findByUniqueId(uniqueId);

                Set<String> likedUser = Objects.requireNonNull(userLikesRedisTemplate.opsForSet().members(redisUserId));

                for (String likedId : likedUser) {
                    UserLike.addLikedId(userInfo, Integer.valueOf(likedId));
                }
            }
        }

    }

    public void backupLikeCountToMySQL(){
        log.info("user like count backup start");

        Set<String> likeCountRedisKey = likeCountRedisTemplate.keys(LikeService.LIKE_COUNT_PREFIX + "*");

        if(likeCountRedisKey != null) {
            for (String data : likeCountRedisKey) {
                Integer userId = Integer.parseInt(data.split(":")[1]);

                if (likeCountRedisTemplate.opsForValue().get(data) == null) break;

                UserInfo userInfo = userService.findById(userId);

                String likeCount = Objects.requireNonNull(likeCountRedisTemplate.opsForValue().get(data));
                userInfo.backupLikeCount(Integer.parseInt(likeCount));
            }
        }

    }
}
