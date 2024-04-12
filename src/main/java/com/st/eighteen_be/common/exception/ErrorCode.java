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
    
    //common
    NOT_NULL(HttpStatus.BAD_REQUEST, "필수 값이 누락되었습니다.");
    
    private final HttpStatus status;
    private final String message;
    
    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}