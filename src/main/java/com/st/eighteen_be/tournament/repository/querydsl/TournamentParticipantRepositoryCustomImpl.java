package com.st.eighteen_be.tournament.repository.querydsl;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.st.eighteen_be.common.exception.ErrorCode;
import com.st.eighteen_be.common.exception.sub_exceptions.data_exceptions.BadRequestException;
import com.st.eighteen_be.common.exception.sub_exceptions.data_exceptions.NotFoundException;
import com.st.eighteen_be.tournament.domain.dto.request.TournamentConstants;
import com.st.eighteen_be.tournament.domain.dto.request.TournamentVoteRequestDTO;
import com.st.eighteen_be.tournament.domain.entity.TournamentEntity;
import com.st.eighteen_be.tournament.domain.entity.TournamentParticipantEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.st.eighteen_be.tournament.domain.entity.QTournamentEntity.tournamentEntity;
import static com.st.eighteen_be.tournament.domain.entity.QTournamentParticipantEntity.tournamentParticipantEntity;

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
    public void updateVotePoints(TournamentVoteRequestDTO voteRequestDTO) {
        int rank = 1;
        
        for (String participantId : voteRequestDTO.getParticipantIdsOrderByRank()) {
            int points = calculatePoints(rank);
            
            if (points > 0) {
                qf.update(tournamentParticipantEntity)
                        .set(tournamentParticipantEntity.score, tournamentParticipantEntity.score.add(points))
                        .where(
                                eqVoteeId(participantId)
                                        .and(eqTournamentNoOfTournamentParticipant(voteRequestDTO.getTournamentNo()))
                        )
                        .execute();
            }
            
            rank++;
        }
        
        em.flush();
        em.clear();
    }
    
    private int calculatePoints(int rank) {
        if (rank <= 0 || rank > TournamentConstants.VOTE_POINT.length) {
            return 0;
        }
        
        return TournamentConstants.VOTE_POINT[rank - 1];
    }
    
    private static BooleanExpression eqTournamentNoOfTournamentParticipant(@NotNull Long tournamentNo) {
        return tournamentParticipantEntity.tournament.tournamentNo.eq(tournamentNo);
    }
    
    @Override
    public void insertVoteRecord(TournamentVoteRequestDTO voteRequestDTO) {
        int rank = 1;
        
        for (String participantId : voteRequestDTO.getParticipantIdsOrderByRank()) {
            int point = calculatePoints(rank);
            
            // 토너먼토와 참여자 정보를 조회합니다.
            TournamentEntity foundTournament = Optional.ofNullable(qf.selectFrom(tournamentEntity)
                                                                           .where(eqTournamentNo(voteRequestDTO.getTournamentNo()))
                                                                           .fetchOne()).orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_TOURNAMENT));
            
            // 투표한 참여자 정보를 조회합니다.
            TournamentParticipantEntity foundTournamentParticipant = Optional.ofNullable(qf.selectFrom(tournamentParticipantEntity)
                                                                                                 .where(eqVoteeId(participantId))
                                                                                                 .fetchOne()).orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_TOURNAMENT_PARTICIPANT));
            
            em.persist(voteRequestDTO.toEntity(foundTournament, foundTournamentParticipant, point));
        }
        
        em.flush();
        em.clear();
    }
    
    private static BooleanExpression eqTournamentNo(Long tournamentNo) {
        return tournamentEntity.tournamentNo.eq(tournamentNo);
    }
    
    private static BooleanExpression eqVoteeId(String voteeId) {
        if (voteeId == null) {
            throw new BadRequestException(ErrorCode.INVALID_PARAMETER);
        }
        
        return tournamentParticipantEntity.userId.eq(voteeId);
    }
}