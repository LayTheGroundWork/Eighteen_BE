package com.st.eighteen_be.user.service;

import com.st.eighteen_be.common.exception.ErrorCode;
import com.st.eighteen_be.common.exception.sub_exceptions.data_exceptions.NotValidException;
import com.st.eighteen_be.jwt.JwtTokenProvider;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LikeService {

    private final RedisTemplate<String,String> redisLikeTemplate;
    private final JwtTokenProvider jwtTokenProvider;

    private static final String PREFIX = "like->";

    public Long count(Integer userId){
        return redisLikeTemplate.opsForHash().size(PREFIX+userId);
    }

    public void addLike(HttpServletRequest request, Integer userId){

        Authentication authentication = authentication(request);
        String key = PREFIX + userId;
        String subKey = authentication.getName();

        if (!redisLikeTemplate.opsForHash().hasKey(key, subKey)) {
            redisLikeTemplate.opsForHash().put(key, subKey, "true");
        }
    }

    public void cancelLike(HttpServletRequest request, Integer userId){

        Authentication authentication = authentication(request);
        String key = PREFIX + userId;
        String subKey = authentication.getName();

        if (redisLikeTemplate.opsForHash().hasKey(key, subKey)) {
            redisLikeTemplate.opsForHash().delete(key, subKey);
        }
    }

    private Authentication authentication(HttpServletRequest request){
        String requestAccessToken = jwtTokenProvider.resolveAccessToken(request);
        if (requestAccessToken == null || !jwtTokenProvider.validateToken(requestAccessToken)) {
            throw new NotValidException(ErrorCode.ACCESS_TOKEN_NOT_VALID);
        }
        return jwtTokenProvider.getAuthentication(requestAccessToken);
    }
}
