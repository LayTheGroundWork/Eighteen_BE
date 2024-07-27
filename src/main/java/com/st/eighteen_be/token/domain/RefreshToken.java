package com.st.eighteen_be.token.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("refreshToken")
@Builder
public class RefreshToken {

    @Id
    private String phoneNumber;

    private String refreshToken;

    @TimeToLive
    private Long expiration;

    public static RefreshToken from(String phoneNumber, String refreshToken, Long expiration) {
        return RefreshToken.builder()
                .phoneNumber(phoneNumber)
                .refreshToken(refreshToken)
                .expiration(expiration / 1000) // @TimeToLive 는 초 단위로 값을 받음
                .build();
    }

}
