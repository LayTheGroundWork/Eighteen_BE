package com.st.eighteen_be.tournament.scheduler;

import com.st.eighteen_be.tournament.domain.redishash.RandomUser;
import com.st.eighteen_be.tournament.service.TournamentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
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
    private final RedisTemplate<String, RandomUser> redisTemplate;

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

    //매일 밤 12시에 유저중에 프로필 이미지가 기본이미지가 아닌 친구들만 골라서 레디스에 올린다.
    // 일단 방식은 회원 총 카운트중에 특정 회원번호
    @Async
    @Scheduled(cron = "0 0  0 * * ?")
    public void pickRandomUser() {
        tournamentService.saveRandomUser();
    }
}
