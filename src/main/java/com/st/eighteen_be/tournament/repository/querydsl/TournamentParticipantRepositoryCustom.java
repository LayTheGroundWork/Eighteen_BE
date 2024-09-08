package com.st.eighteen_be.tournament.repository.querydsl;

import com.st.eighteen_be.tournament.domain.dto.request.TournamentVoteRequestDTO;

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
    void updateVotePoints(TournamentVoteRequestDTO voteRequestDTOs, String loginedUserId);

    void insertVoteRecord(TournamentVoteRequestDTO voteRequestDTOs, String loginedUserId);
}
