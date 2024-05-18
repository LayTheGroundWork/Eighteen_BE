package com.st.eighteen_be.tournament.service;

import com.st.eighteen_be.tournament.repository.GameEntityRepository;
import com.st.eighteen_be.tournament.repository.TournamentEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * packageName    : com.st.eighteen_be.tournament.api
 * fileName       : TournamentService
 * author         : ipeac
 * date           : 24. 5. 15.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 5. 15.        ipeac       최초 생성
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TournamentAsyncService {
    private TournamentEntityRepository tournamentEntityRepository;
    private GameEntityRepository gameEntityRepository;
    
    public void test() {
        System.out.println("test");
    }
}