package com.st.eighteen_be.tournament.repository.querydsl;

import com.querydsl.core.group.GroupBy;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.st.eighteen_be.tournament.domain.dto.response.QTournamentSearchResponseDTO;
import com.st.eighteen_be.tournament.domain.dto.response.QTournamentSearchResponseDTO_TournamentWinnerResponseDTO;
import com.st.eighteen_be.tournament.domain.dto.response.TournamentSearchResponseDTO;
import com.st.eighteen_be.tournament.domain.entity.QTournamentEntity;
import com.st.eighteen_be.tournament.domain.entity.QTournamentParticipantEntity;
import com.st.eighteen_be.tournament_winner.domain.QTournamentWinnerEntity;
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
public class TournamentRepositoryCustomImpl implements TournamentRepositoryCustom {
    
    private final JPAQueryFactory qf;
    
    @Override
    public List<TournamentSearchResponseDTO> findTournamentMainInfos() {
        QTournamentEntity tournament = QTournamentEntity.tournamentEntity;
        QTournamentWinnerEntity tournamentWinner = QTournamentWinnerEntity.tournamentWinnerEntity;
        QTournamentParticipantEntity tournamentParticipant = QTournamentParticipantEntity.tournamentParticipantEntity;
        
        return qf
                .from(tournamentWinner)
                .join(tournamentParticipant).on(tournamentWinner.participantNo.eq(tournamentParticipant.participantNo))
                .join(tournament).on(tournament.tournamentNo.eq(tournamentWinner.winningTournamentNo))
                .transform(GroupBy.groupBy(tournament.category)
                        .list(new QTournamentSearchResponseDTO(
                                tournament.category,
                                GroupBy.list(new QTournamentSearchResponseDTO_TournamentWinnerResponseDTO(
                                                tournamentWinner.winningTournamentNo,
                                                tournament.season,
                                                tournamentParticipant.userImageUrl
                                        )
                                ))
                        ));
    }
}
