package com.st.eighteen_be.user.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RedisHash(value = "ID")
public class UserLike {

    @Id
    private Integer pressedUserId;

    @Indexed
    private String userUniqueId;

    public static UserLike from(Integer pressedUserId, String userUniqueId) {
        return UserLike.builder()
                .pressedUserId(pressedUserId)
                .userUniqueId(userUniqueId)
                .build();
    }
}
