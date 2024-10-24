package com.st.eighteen_be.tournament.domain.redishash;

import com.st.eighteen_be.tournament.domain.entity.TournamentEntity;
import com.st.eighteen_be.tournament.domain.entity.TournamentParticipantEntity;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.util.Objects;

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
@RedisHash("mostLikedUser")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class MostLikedUserRedisHash {
    @Id
    private String uid;

    @Indexed
    private String userId;

    @Indexed
    private String category;

    private String profileImageUrl;

    public static MostLikedUserRedisHash of(String userId, String profileImageUrl, String category) {
        return MostLikedUserRedisHash.builder()
                       .uid(createUid(userId, category))
                       .userId(userId)
                       .category(category)
                       .profileImageUrl(profileImageUrl)
                       .build();
    }

    public static String createUid(String userId, String category) {
        return "mostLikedUser:" + userId + ":" + category;
    }

    public TournamentParticipantEntity from(TournamentEntity newTournament) {
        Objects.requireNonNull(newTournament, "newTournament must not be null");

        return TournamentParticipantEntity.builder()
                .tournament(newTournament)
                .userId(userId)
                .userImageUrl(profileImageUrl)
                .score(0)
                .build();
    }
}
