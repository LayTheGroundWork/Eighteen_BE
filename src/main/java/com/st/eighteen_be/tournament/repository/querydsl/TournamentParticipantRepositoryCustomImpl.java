package com.st.eighteen_be.tournament.repository.querydsl;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.st.eighteen_be.common.exception.ErrorCode;
import com.st.eighteen_be.common.exception.sub_exceptions.data_exceptions.BadRequestException;
import com.st.eighteen_be.tournament.api.TournamentVoteRequestDTO;
import com.st.eighteen_be.tournament.domain.entity.QTournamentParticipantEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * packageName    : com.st.eighteen_be.tournament.repository.querydsl
 * fileName       : TournamentRepsitoryCustomImpl
 * author         : ipeac
 * date           : 24. 5. 24.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 5. 24.        ipeac       최초 생성
 */
@Repository
@RequiredArgsConstructor
public class TournamentParticipantRepositoryCustomImpl implements TournamentParticipantRepositoryCustom {
    
    @PersistenceContext
    private final EntityManager em;
    private final JPAQueryFactory qf;
    
    @Override
    public void updateVotePoints(List<TournamentVoteRequestDTO> voteRequestDTOs) {
        
        QTournamentParticipantEntity tournamentParticipantEntity = QTournamentParticipantEntity.tournamentParticipantEntity;
        
        for (TournamentVoteRequestDTO voteRequest : voteRequestDTOs) {
            qf.update(tournamentParticipantEntity)
                    .set(tournamentParticipantEntity.score, tournamentParticipantEntity.score.add(voteRequest.getVotePoint()))
                    .where(eqParticipantId(voteRequest, tournamentParticipantEntity))
                    .execute();
        }
        
        em.flush();
        em.clear();
    }
    
    private static BooleanExpression eqParticipantId(@NonNull TournamentVoteRequestDTO voteRequest, QTournamentParticipantEntity tournamentParticipantEntity) {
        if (voteRequest.getParticipantId() == null || voteRequest.getVotePoint() == 0) {
            throw new BadRequestException(ErrorCode.INVALID_PARAMETER);
        }
        
        return tournamentParticipantEntity.userId.eq(voteRequest.getParticipantId());
    }
}