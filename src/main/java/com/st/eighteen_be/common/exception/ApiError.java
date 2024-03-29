package com.st.eighteen_be.common.exception;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

/**
 * packageName    : com.st.eighteen_be.config.exception
 * fileName       : ApiError
 * author         : ipeac
 * date           : 2024-03-27
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-03-27        ipeac       최초 생성
 */
public record ApiError(
        HttpStatus status,
        String error,
        String message,
        String path,
        LocalDateTime timestamp
) {}