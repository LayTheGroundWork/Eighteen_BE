package com.st.eighteen_be.token.domain;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("refreshToken")
@Builder
public class RefreshToken {

    @Id
    private String id;

    private String refreshToken;

    @TimeToLive
    private Long expiration;

    public static RefreshToken from(String phoneNumber, String refreshToken, Long expiration) {
        return RefreshToken.builder()
                .id(phoneNumber)
                .refreshToken(refreshToken)
                .expiration(expiration / 1000)
                .build();
    }

}
