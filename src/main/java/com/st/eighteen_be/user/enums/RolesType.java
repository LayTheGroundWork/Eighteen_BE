package com.st.eighteen_be.user.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * packageName    : com.st.eighteen_be.user.enums
 * fileName       : RolesType
 * author         : ehgur
 * date           : 2024-08-06
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-08-06        ehgur             최초 생성
 */

@RequiredArgsConstructor
@Getter
public enum RolesType {
    USER("USER","일반 사용자")

    ;

    private final String key;
    private final String value;
}
