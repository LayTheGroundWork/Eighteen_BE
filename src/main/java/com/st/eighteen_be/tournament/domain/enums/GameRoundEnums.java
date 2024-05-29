package com.st.eighteen_be.tournament.domain.enums;

import com.st.eighteen_be.common.exception.ErrorCode;
import com.st.eighteen_be.common.exception.sub_exceptions.data_exceptions.NotFoundException;
import lombok.Getter;

/**
 * packageName    : com.st.eighteen_be.tournament.domain.entity
 * fileName       : GameRound
 * author         : ipeac
 * date           : 24. 5. 17.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 5. 17.        ipeac       최초 생성
 */
@Getter
public enum GameRoundEnums {
    ROUND_16(16, "16강"),
    ROUND_8(8, "8강"),
    ROUND_4(4, "4강"),
    FINAL(1, "결승");
    
    private final int round;
    private final String description;
    
    GameRoundEnums(int round, String description) {
        this.round = round;
        this.description = description;
    }
    
    public static GameRoundEnums getGameRound(int round) {
        for (GameRoundEnums gameRound : values()) {
            if (gameRound.round == round) {
                return gameRound;
            }
        }
        
        throw new NotFoundException(ErrorCode.NOT_FOUND_GAME_ROUND);
    }
}