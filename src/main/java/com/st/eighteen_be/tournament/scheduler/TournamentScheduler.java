package com.st.eighteen_be.tournament.scheduler;

import com.st.eighteen_be.tournament.service.TournamentService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

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
public class TournamentScheduler {
    private final TournamentService tournamentService;
    
    @Async
    @Scheduled(cron = "0 0  9 ? * MON")
    protected void startNewTournaments() {
        tournamentService.startTournament();
    }
    
    @Async
    @Scheduled(cron = "0 0 12 ? * SUN")
    protected void endTournaments() {
        tournamentService.endLastestTournaments();
    }
}