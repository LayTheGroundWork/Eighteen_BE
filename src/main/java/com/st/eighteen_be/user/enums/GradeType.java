package com.st.eighteen_be.user.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * packageName    : com.st.eighteen_be.enums
 * fileName       : Grade_Type
 * author         : ehgur
 * date           : 2024-04-15
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-04-15        ehgur             최초 생성
 */

@RequiredArgsConstructor
@Getter
public enum GradeType {
    FIRST_GRADE(1,"1학년"),
    SECOND_GRADE(2,"2학년"),
    THIRD_GRADE(3,"3학년");

    private final int grade;
    private final String title;
}
