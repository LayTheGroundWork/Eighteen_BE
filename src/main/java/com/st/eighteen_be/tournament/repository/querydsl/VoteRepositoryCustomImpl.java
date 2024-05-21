package com.st.eighteen_be.tournament.repository.querydsl;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.st.eighteen_be.tournament.domain.dto.response.QTournamentVoteResultResponseDTO;
import com.st.eighteen_be.tournament.domain.dto.response.TournamentVoteResultResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.st.eighteen_be.tournament.domain.entity.QVoteEntity.voteEntity;

@Repository
@RequiredArgsConstructor
public class VoteRepositoryCustomImpl implements VoteRepositoryCustom {
    
    private final JPAQueryFactory qf;
    
    public List<TournamentVoteResultResponseDTO> findTournamentVoteResult(Long tournamentNo) {
        qf.select(new QTournamentVoteResultResponseDTO(voteEntity.tournament.tournamentNo, voteEntity.participant.userId, voteEntity.count()))
                .from(voteEntity)
                .where(eqTournamentNo(tournamentNo))
                .groupBy(voteEntity.tournament.tournamentNo, voteEntity.participant)
                .fetch();
        return List.of();
    }
    
    private BooleanExpression eqTournamentNo(Long tournamentId) {
        if (tournamentId == null) {
            return null;
        }
        
        return voteEntity.tournament.tournamentNo.eq(tournamentId);
    }
}