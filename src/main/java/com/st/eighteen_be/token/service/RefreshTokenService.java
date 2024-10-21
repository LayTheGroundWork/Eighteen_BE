package com.st.eighteen_be.token.service;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RedisTemplate<String, String> redisRefreshTokenTemplate;

    private static final String REFRESH_TOKEN = "refreshToken:";

    public void setRefreshToken(Authentication authentication, String refreshToken, long expiration) {
        redisRefreshTokenTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(refreshToken.getClass()));
        redisRefreshTokenTemplate.opsForValue().set(
                REFRESH_TOKEN + authentication.getName(), refreshToken, expiration, TimeUnit.MILLISECONDS);
    }

    public String getRefreshToken(String key) {
        return redisRefreshTokenTemplate.opsForValue().get(REFRESH_TOKEN+key);
    }

    public void deleteRefreshToken(String key) {
        redisRefreshTokenTemplate.delete(REFRESH_TOKEN + key);
    }

    public boolean hasKeyRefreshToken(String key) {
        return Boolean.TRUE.equals(redisRefreshTokenTemplate.hasKey(REFRESH_TOKEN+key));
    }
}
