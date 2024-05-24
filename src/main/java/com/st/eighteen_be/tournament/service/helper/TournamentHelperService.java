package com.st.eighteen_be.tournament.service.helper;

import com.st.eighteen_be.tournament.domain.entity.TournamentParticipantEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * packageName    : com.st.eighteen_be.tournament.service.helper
 * fileName       : TournamentHelperService
 * author         : ipeac
 * date           : 24. 5. 19.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 5. 19.        ipeac       최초 생성
 */
@Component
@Slf4j
public final class TournamentHelperService {

    public static List<TournamentParticipantEntity> pickRandomUser() {
        //TODO 랜덤 유저 16명을 뽑는 로직
        return Collections.unmodifiableList(new ArrayList<>());
    }
}