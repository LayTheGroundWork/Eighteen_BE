package com.st.eighteen_be.jwt;

import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class JwtTokenDto {
    private String grantType;
    private String accessToken;
    private long accessTokenExpiresIn;
    private String refreshToken;
}
