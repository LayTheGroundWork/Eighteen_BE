package com.st.eighteen_be.tournament.batch;

import com.st.eighteen_be.tournament.domain.enums.TournamentCategoryEnums;
import com.st.eighteen_be.tournament.service.TournamentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * packageName    : com.st.eighteen_be.tournament.batch
 * fileName       : TournamentBatchTest
 * author         : ipeac
 * date           : 24. 5. 19.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 5. 19.        ipeac       최초 생성
 */
@ExtendWith(MockitoExtension.class)
public class TournamentBatchTest {
    @InjectMocks
    private TournamentScheduler tournamentScheduler;
    
    @Mock
    private TournamentService tournamentService;
    
    @Test
    @DisplayName("startNewTournaments 스케쥴러 정상 동작 테스트 - enums 길이만큼 startTournament 호출")
    void When_startNewTournaments_enumLength_Then_startTournament() {
        //when
        tournamentScheduler.startNewTournaments();
        
        //then
        verify(tournamentService, times(TournamentCategoryEnums.values().length)).startTournament(any());
    }
    
    @Test
    @DisplayName("endTournaments 스케쥴러 정상 동작 테스트")
    void When_endTournaments_Then_endLastTournament() {
        //when
        tournamentScheduler.endTournaments();
        
        //then
        verify(tournamentService, times(1)).endLastTournament();
    }
}