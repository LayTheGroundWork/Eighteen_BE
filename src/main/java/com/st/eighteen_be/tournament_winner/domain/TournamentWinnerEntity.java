package com.st.eighteen_be.tournament_winner.domain;

import com.st.eighteen_be.tournament.domain.entity.TournamentParticipantEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
@Builder
@Entity
public class TournamentWinnerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private String userId;
    
    @Column(name = "participant_no", nullable = false)
    private Long participantNo;
    
    @OneToOne
    @JoinColumn(name = "participant_no", referencedColumnName = "participant_no", insertable = false, updatable = false)
    private TournamentParticipantEntity participant;
    
    //우승한 토너번호 번호
    @Column(name = "winning_tournament_no", nullable = false)
    private Long winningTournamentNo;
    
    public static TournamentWinnerEntity from(TournamentParticipantEntity participant) {
        return TournamentWinnerEntity.builder()
                .userId(participant.getUserId())
                .winningTournamentNo(participant.getTournament().getTournamentNo())
                .build();
    }
}
