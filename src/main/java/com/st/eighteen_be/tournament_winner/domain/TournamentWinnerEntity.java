package com.st.eighteen_be.tournament_winner.domain;

import com.st.eighteen_be.tournament.domain.entity.TournamentParticipantEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * packageName    : com.st.eighteen_be.tournament_winner.domain
 * fileName       : TournamentWinnerEntity
 * author         : ipeac
 * date           : 24. 10. 20.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 10. 20.        ipeac       최초 생성
 */
@Getter
@Table(name = "tournament_winner")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PRIVATE)
@Entity
public class TournamentWinnerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private String userId;
    
    @Builder.Default
    @Column(name = "winner_count", nullable = false)
    private Integer winnerCount = 0 ;
    
    public static TournamentWinnerEntity from(TournamentParticipantEntity participant) {
        return TournamentWinnerEntity.builder()
                .userId(participant.getUserId())
                .winnerCount(0)
                .build();
    }
    
    public void addWinnerCount() {
        this.winnerCount++;
    }
}
