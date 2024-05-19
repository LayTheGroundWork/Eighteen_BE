package com.st.eighteen_be.jwt;

import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class JwtTokenDto {
    private String grantType;
    private String accessToken;

    public static JwtTokenDto from(String accessToken) {
        return JwtTokenDto.builder()
                .grantType(JwtTokenProvider.TOKEN_PREFIX)
                .accessToken(accessToken)
                .build();
    }
}
