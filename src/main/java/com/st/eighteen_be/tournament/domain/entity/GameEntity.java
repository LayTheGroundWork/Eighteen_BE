package com.st.eighteen_be.tournament.domain.entity;

import com.st.eighteen_be.common.basetime.BaseEntity;
import com.st.eighteen_be.common.converter.GameRoundConverter;
import com.st.eighteen_be.tournament.domain.enums.GameRoundEnums;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;

@Getter
@Entity
@Table(name = "GAME")
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GameEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("경기고유번호")
    @Column(name = "GAME_NO")
    private Long gameNo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @Comment("토너먼트고유번호")
    @JoinColumn(name = "TOURNAMENT_NO", nullable = false)
    private TournamentEntity tournamentEntity;

    @Size(max = 200)
    @NotNull
    @Comment("참여자 유저 아이디1")
    @Column(name = "PARTICIPANT_ID_1", nullable = false, length = 200)
    private String participantId1;

    @Size(max = 200)
    @NotNull
    @Comment("참여자 유저 아이디2")
    @Column(name = "PARTICIPANT_ID_2", nullable = false, length = 200)
    private String participantId2;

    @NotNull
    @Comment("라운드(16, 8 ,4 , 1)")
    @Convert(converter = GameRoundConverter.class)
    @Column(name = "ROUND", nullable = false)
    private GameRoundEnums round;

    public static GameEntity createSixteenthRoundGame(TournamentEntity tournamentEntity, String participantId1, String participantId2) {
        return GameEntity.builder()
                .tournamentEntity(tournamentEntity)
                .participantId1(participantId1)
                .participantId2(participantId2)
                .round(GameRoundEnums.ROUND_16)
                .build();
    }
}