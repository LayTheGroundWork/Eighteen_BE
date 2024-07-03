package com.st.eighteen_be.user.service;

import com.st.eighteen_be.common.exception.ErrorCode;
import com.st.eighteen_be.common.exception.sub_exceptions.data_exceptions.NotValidException;
import com.st.eighteen_be.jwt.JwtTokenProvider;
import com.st.eighteen_be.user.domain.UserLike;
import com.st.eighteen_be.user.repository.UserLikeRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final UserLikeRepository userLikeRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public void countUp(HttpServletRequest request, Integer userId){
        String requestAccessToken = jwtTokenProvider.resolveAccessToken(request);

        if (!jwtTokenProvider.validateToken(requestAccessToken)) {
            throw new NotValidException(ErrorCode.ACCESS_TOKEN_NOT_VALID);
        }
        Authentication authentication = jwtTokenProvider.getAuthentication(requestAccessToken);

        userLikeRepository.save(UserLike.from(userId,authentication.getName()));

    }
}
