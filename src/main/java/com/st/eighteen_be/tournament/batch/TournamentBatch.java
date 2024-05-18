package com.st.eighteen_be.tournament.batch;

import com.st.eighteen_be.tournament.domain.entity.GameEntity;
import com.st.eighteen_be.tournament.domain.entity.TournamentEntity;
import com.st.eighteen_be.tournament.repository.GameEntityRepository;
import com.st.eighteen_be.tournament.repository.TournamentEntityRepository;
import com.st.eighteen_be.tournament.service.TournamentService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * packageName    : com.st.eighteen_be.tournament.batch
 * fileName       : TournamentBatch
 * author         : ipeac
 * date           : 24. 5. 18.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 5. 18.        ipeac       최초 생성
 */
@Service
@RequiredArgsConstructor
public class TournamentBatch {
    private final TournamentService tournamentService;

    private final TournamentEntityRepository tournamentEntityRepository;
    private final GameEntityRepository gameEntityRepository;

    @Async
    @Scheduled(cron = "0 0  9 ? * MON")
    public void startNewTournaments() {
        tournamentService.test();
    }

    public void makeTournament(TournamentEntity tournamentEntity) {
        tournamentEntityRepository.save(tournamentEntity);
    }

    public void makeGames(List<GameEntity> games) {
        gameEntityRepository.saveAll(games);
    }
}