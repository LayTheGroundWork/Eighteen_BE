package com.st.eighteen_be.tournament_winner.repository;

import com.st.eighteen_be.tournament_winner.domain.TournamentWinnerEntity;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

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
    Optional<TournamentWinnerEntity> findByUserId(@Size(max = 50) String userId);
}
