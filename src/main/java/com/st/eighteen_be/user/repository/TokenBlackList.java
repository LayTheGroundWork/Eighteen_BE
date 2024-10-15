package com.st.eighteen_be.user.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class TokenBlackList {

    private final RedisTemplate<String, String> redisBlackListTemplate;

    private static final String BLACKLIST = "black->";

    public void setBlackList(String key, String phoneNumber, Long milliSeconds) {
        redisBlackListTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(phoneNumber.getClass()));
        redisBlackListTemplate.opsForValue().set(BLACKLIST + key, phoneNumber, milliSeconds, TimeUnit.MILLISECONDS);
    }

    public Object getBlackList(String key) {
        return redisBlackListTemplate.opsForValue().get(BLACKLIST+key);
    }

    public boolean deleteBlackList(String key) {
        return Boolean.TRUE.equals(redisBlackListTemplate.delete(BLACKLIST+key));
    }

    public boolean hasKeyBlackList(String key) {
        return Boolean.TRUE.equals(redisBlackListTemplate.hasKey(BLACKLIST+key));
    }
}
