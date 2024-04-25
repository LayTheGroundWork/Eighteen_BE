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

    //chat
    NOT_FOUND_CHATROOM(HttpStatus.NOT_FOUND, "해당하는 채팅방을 찾을 수 없습니다."),
    NOT_FOUND_CHAT_MESSAGE(HttpStatus.NOT_FOUND, "해당하는 채팅 메시지를 찾을 수 없습니다."),
    CHATROOM_SAME_USER(HttpStatus.BAD_REQUEST, "같은 사용자입니다."),

    //tournament
    NOT_FOUND_GAME_ROUND(HttpStatus.NOT_FOUND, "해당하는 경기 라운드를 찾을 수 없습니다."),
    NOT_FOUND_CATEGORY(HttpStatus.NOT_FOUND, "해당하는 카테고리를 찾을 수 없습니다."),
    INVALID_USER_COUNT(HttpStatus.BAD_REQUEST, "유저 수가 올바르지 않습니다."),
    NOT_FOUND_TOURNAMENT(HttpStatus.NOT_FOUND, "해당하는 토너먼트를 찾을 수 없습니다."),
    NOT_FOUND_TOURNAMENT_PARTICIPANT(HttpStatus.NOT_FOUND, "해당하는 토너먼트 참가자를 찾을 수 없습니다."),

    //common
    NOT_NULL(HttpStatus.BAD_REQUEST, "필수 값이 누락되었습니다."),
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "잘못된 파라미터입니다."),

    NOT_FOUND_CHATROOM_TYPE(HttpStatus.NOT_FOUND, "채팅방 타입을 찾을 수 없습니다."),
    EXISTS_MEMBER(HttpStatus.CONFLICT, "이미 존재하는 회원입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "Spring security unauthorized..."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "Spring security forbidden..."),

    ;

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}