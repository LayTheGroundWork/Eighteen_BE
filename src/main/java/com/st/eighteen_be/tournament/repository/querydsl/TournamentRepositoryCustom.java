package com.st.eighteen_be.tournament.repository.querydsl;

import com.st.eighteen_be.tournament.domain.dto.response.TournamentSearchResponseDTO;

import java.util.List;

/**
 * packageName    : com.st.eighteen_be.tournament.repository.querydsl
 * fileName       : VoteRepositoryCustom
 * author         : ipeac
 * date           : 24. 5. 22.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 5. 22.        ipeac       최초 생성
 */
public interface TournamentRepositoryCustom {
    List<TournamentSearchResponseDTO> findTournamentMainInfos();
}
