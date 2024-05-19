package com.st.eighteen_be.token.service;

import com.st.eighteen_be.common.exception.ErrorCode;
import com.st.eighteen_be.common.exception.sub_exceptions.data_exceptions.TokenNotFoundException;
import com.st.eighteen_be.jwt.JwtTokenProvider;
import com.st.eighteen_be.token.domain.RefreshToken;
import com.st.eighteen_be.token.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public RefreshToken saveOrUpdate(Authentication authentication) {
        return refreshTokenRepository.save(RefreshToken.from(
                authentication.getName(),
                jwtTokenProvider.generateToken(authentication),
                JwtTokenProvider.REFRESH_TOKEN_EXPIRE_TIME)
        );
    }
    public RefreshToken findRefreshTokenById(String username) {
        return refreshTokenRepository.findById(username).orElseThrow(
                () -> new TokenNotFoundException(ErrorCode.TOKEN_NOT_FOUND)
        );
    }

    public void deleteRefreshTokenById(String username) {
        refreshTokenRepository.deleteById(username);
    }
}
