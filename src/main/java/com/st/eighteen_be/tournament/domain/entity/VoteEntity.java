package com.st.eighteen_be.tournament.domain.entity;

import com.st.eighteen_be.common.basetime.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;

@Getter
@Entity
@Table(name = "VOTE", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"TOURNAMENT_NO", "PARTICIPANT_NO", "VOTER_ID"})
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class VoteEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("투표고유번호")
    @Column(name = "VOTE_NO")
    private Long voteNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TOURNAMENT_NO", nullable = false)
    private TournamentEntity tournament;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARTICIPANT_NO", nullable = false)
    private TournamentParticipantEntity participant;

    @Size(max = 200)
    @Comment("투표자 유저 아이디")
    @Column(name = "VOTER_ID", length = 200)
    private String voterId;
}