package com.st.eighteen_be.tournament.domain.redishash;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

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
@RedisHash("randomUser")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PUBLIC)
public class RandomUser {
    @Id
    private String uid;

    @Indexed
    private Integer userNo;
    private String profileImageUrl;

    public static RandomUser of(int userNo, String profileImageUrl) {
        return RandomUser.builder()
                .uid(createUid(userNo))
                .userNo(userNo)
                .profileImageUrl(profileImageUrl)
                .build();
    }

    public static String createUid(int userNo) {
        return "randomUser:" + userNo;
    }
}
