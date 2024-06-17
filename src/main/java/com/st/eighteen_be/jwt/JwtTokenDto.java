package com.st.eighteen_be.jwt;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class JwtTokenDto {
    @Schema(description = "인증 방식", example = "Bearer 또는 Basic")
    private String grantType;

    @Schema(description = "회원한테 발급된 엑세스 토큰", example = "Bearer-wer.werw.wer")
    private String accessToken;

    @Schema(description = "엑세스 토큰 만료시간(밀리초)", example = "1729491982")
    private long accessTokenExpiresIn;

    @Schema(description = "엑세스 토큰 재발급을 위한 토큰", example = "wer.werw.wer")
    private String refreshToken;
}
