package com.st.eighteen_be.tournament.domain.entity;

import com.st.eighteen_be.common.basetime.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;

@Getter
@Entity
@Table(name = "TOURNAMENT_PARTICIPANT")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class TournamentParticipantEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("토너먼트 참가자고유번호")
    @Column(name = "PARTICIPANT_NO")
    private Long participantNo;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @Comment("토너먼트 고유번호")
    @JoinColumn(name = "TOURNAMENT_NO")
    private TournamentEntity tournament;
    
    @Size(max = 200)
    @Comment("토너먼트 참가자 ID")
    @Column(name = "USER_ID", length = 200)
    private String userId;
    
    @Builder.Default
    @Comment("토너먼트 참가자 점수")
    @Column(name = "SCORE")
    private long score = 0;
    
    public static TournamentParticipantEntity of(String userId) {
        return TournamentParticipantEntity.builder()
                .userId(userId)
                .build();
    }
    
    public static TournamentParticipantEntity of(String userId, TournamentEntity tournament) {
        return TournamentParticipantEntity.builder()
                .userId(userId)
                .tournament(tournament)
                .build();
    }
}