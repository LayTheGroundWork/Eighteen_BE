package com.st.eighteen_be.jwt;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Jwt {
    private String grantType;
    private String accessToken;
    private String refreshToken;
}
