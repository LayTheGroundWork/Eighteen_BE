package com.st.eighteen_be.common.exception.base_exceptions;

import com.st.eighteen_be.common.exception.ErrorCode;
import lombok.Getter;

/**
 * packageName    : com.st.eighteen_be.config.exception
 * fileName       : CustomeRuntimeException
 * author         : ipeac
 * date           : 2024-03-27
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-03-27        ipeac       최초 생성
 */
@Getter
public class CustomeRuntimeException extends RuntimeException {
    private final ErrorCode errorCode;
    
    public CustomeRuntimeException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}