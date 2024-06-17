package com.st.eighteen_be.jwt;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class JwtRequestDto {

    @Schema(description = "회원 인증 토큰", example = "Bearer-werwe.werwe.werw")
    private String accessToken;

    @Schema(description = "엑세스토큰 재발급을 위한 토큰", example = "werw.werw.werw")
    private String refreshToken;

    @Builder
    public JwtRequestDto(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
