package com.st.eighteen_be.tournament.domain.redishash;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serial;
import java.io.Serializable;

/**
 * packageName    : com.st.eighteen_be.tournament.domain.redishash
 * fileName       : RandomUser
 * author         : ipeac
 * date           : 24. 8. 28.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 8. 28.        ipeac       최초 생성
 */
@RedisHash("RandomUser")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PUBLIC)
public class RandomUser  implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private String userId;
    private String profileImageUrl;

    public static RandomUser of(String userId, String profileImageUrl) {
        return RandomUser.builder()
                .userId(userId)
                .profileImageUrl(profileImageUrl)
                .build();
    }
}
