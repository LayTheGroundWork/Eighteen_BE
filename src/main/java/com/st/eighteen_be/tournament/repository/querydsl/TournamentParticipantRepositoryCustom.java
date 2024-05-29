package com.st.eighteen_be.tournament.repository.querydsl;

import com.st.eighteen_be.tournament.api.TournamentVoteRequestDTO;

import java.util.List;

/**
 * packageName    : com.st.eighteen_be.tournament.repository
 * fileName       : TournamentParticipantEntityRepositoryCustom
 * author         : ipeac
 * date           : 24. 5. 27.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 5. 27.        ipeac       최초 생성
 */
public interface TournamentParticipantRepositoryCustom {
    void updateVotePoints(List<TournamentVoteRequestDTO> voteRequestDTOs);
    
    void insertVoteRecord(List<TournamentVoteRequestDTO> voteRequestDTOs);
}