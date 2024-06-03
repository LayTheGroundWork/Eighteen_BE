package com.st.eighteen_be.token.service;

import com.st.eighteen_be.jwt.JwtTokenProvider;
import com.st.eighteen_be.token.domain.RefreshToken;
import com.st.eighteen_be.token.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${jwt.refreshTokenExpireTime}")
    private long refreshTokenExpireTime;

    public RefreshToken saveOrUpdate(Authentication authentication) {
        return refreshTokenRepository.save(RefreshToken.from(
                authentication.getName(),
                jwtTokenProvider.generateToken(authentication).getRefreshToken(),
                refreshTokenExpireTime)
        );
    }
    public RefreshToken findRefreshTokenById(String username) {
        return refreshTokenRepository.findById(username).orElseThrow(
                () -> new RuntimeException("로그아웃 된 사용자입니다.")
        );
    }

    public void deleteRefreshTokenById(String username) {
        refreshTokenRepository.deleteById(username);
    }
}
