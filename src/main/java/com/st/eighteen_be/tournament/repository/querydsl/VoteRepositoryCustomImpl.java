package com.st.eighteen_be.tournament.repository.querydsl;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.st.eighteen_be.tournament.domain.dto.response.QTournamentVoteResultResponseDTO;
import com.st.eighteen_be.tournament.domain.dto.response.TournamentVoteResultResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.st.eighteen_be.tournament.domain.entity.QTournamentParticipantEntity.tournamentParticipantEntity;
import static com.st.eighteen_be.tournament.domain.entity.QVoteEntity.voteEntity;

@Repository
@RequiredArgsConstructor
public class VoteRepositoryCustomImpl implements VoteRepositoryCustom {
    
    private final JPAQueryFactory qf;
    
    public List<TournamentVoteResultResponseDTO> findTournamentVoteResult(Long tournamentNo) {
        QTournamentVoteResultResponseDTO dto = new QTournamentVoteResultResponseDTO(
                tournamentParticipantEntity.userId,
                tournamentParticipantEntity.score);
        
        return qf.select(dto)
                .from(voteEntity)
                .leftJoin(tournamentParticipantEntity)
                .on(eqJoinParticipantNo())
                .where(eqTournamentNo(tournamentNo))
                .groupBy(voteEntity.tournament.tournamentNo, voteEntity.participant)
                .orderBy(tournamentParticipantEntity.score.desc())
                .fetch();
    }
    
    private static BooleanExpression eqJoinParticipantNo() {
        return voteEntity.participant.participantNo.eq(tournamentParticipantEntity.participantNo);
    }
    
    private static BooleanExpression eqTournamentNo(Long tournamentId) {
        if (tournamentId == null) {
            return null;
        }
        
        return voteEntity.tournament.tournamentNo.eq(tournamentId);
    }
}