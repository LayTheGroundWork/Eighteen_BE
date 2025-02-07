package com.st.eighteen_be.tournament_winner.repository;

import com.st.eighteen_be.tournament_winner.domain.TournamentWinnerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * packageName    : com.st.eighteen_be.tournament_winner.repository
 * fileName       : TournamentWinnerRepository
 * author         : ipeac
 * date           : 24. 10. 20.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 10. 20.        ipeac       최초 생성
 */
public interface TournamentWinnerRepository extends JpaRepository<TournamentWinnerEntity, Long> {
    boolean existsByUserId(String userId);
}
