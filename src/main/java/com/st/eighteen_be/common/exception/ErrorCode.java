package com.st.eighteen_be.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * packageName    : com.st.eighteen_be.config.exception
 * fileName       : ErrorCode
 * author         : ipeac
 * date           : 2024-03-27
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-03-27        ipeac       최초 생성
 */
@Getter
public enum ErrorCode {
    //커스텀 에러코드 정의부분
    TEST_ERROR(HttpStatus.NOT_FOUND, "테스트 에러"),
    NOT_FOUND_CHATROOM_TYPE(HttpStatus.NOT_FOUND, "채팅방 타입을 찾을 수 없습니다."),
    SIGN_UP_EXISTS_USER(HttpStatus.CONFLICT, "계정이 존재합니다."),
    SIGN_IN_NOT_FOUND_USER(HttpStatus.NOT_FOUND, "회원 정보가 존재하지 않습니다."),
    AUTHENTICATION_NUMBER_MISMATCH(HttpStatus.BAD_REQUEST, "인증 번호가 일치하지 않습니다"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "Spring security unauthorized..."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "Spring security forbidden..."),
    TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "토큰을 찾을 수 없습니다."),

    ;

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}