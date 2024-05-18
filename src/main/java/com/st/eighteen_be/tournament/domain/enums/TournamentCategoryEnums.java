package com.st.eighteen_be.tournament.domain.enums;

import com.st.eighteen_be.common.exception.ErrorCode;
import com.st.eighteen_be.common.exception.sub_exceptions.data_exceptions.NotFoundException;
import lombok.Getter;

/**
 * packageName    : com.st.eighteen_be.tournament.domain.entity
 * fileName       : TournamentCategoryEnums
 * author         : ipeac
 * date           : 24. 5. 18.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 5. 18.        ipeac       최초 생성
 */
@Getter
public enum TournamentCategoryEnums {
    SPORTS("SPORTS"),
    GAME("GAME"),
    MUSIC("MUSIC"),
    MOVIE("MOVIE"),
    ETC("ETC");

    private final String category;

    TournamentCategoryEnums(String category) {
        this.category = category;
    }

    public static TournamentCategoryEnums findByCategory(String category) {
        for (TournamentCategoryEnums value : values()) {
            if (value.getCategory().equals(category)) {
                return value;
            }
        }

        throw new NotFoundException(ErrorCode.NOT_FOUND_CATEGORY);
    }
}