package com.st.eighteen_be.tournament.service.helper;

import com.st.eighteen_be.common.exception.ErrorCode;
import com.st.eighteen_be.common.exception.sub_exceptions.data_exceptions.BadRequestException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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
public final class TournamentHelperService {

    public static List<String> pickRandomUser() {
        //TODO 랜덤 유저 16명을 뽑는 로직
        return new ArrayList<>();
    }

    public static void checkSixteenthRoundUserCount(List<String> users) {
        if (users.size() != 16) {
            throw new BadRequestException(ErrorCode.INVALID_USER_COUNT);
        }
    }
}