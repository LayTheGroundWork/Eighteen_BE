package com.st.eighteen_be.common.exception.sub_exceptions.data_exceptions;

import com.st.eighteen_be.common.exception.ErrorCode;
import com.st.eighteen_be.common.exception.base_exceptions.CustomeRuntimeException;
import lombok.Getter;

/**
 * packageName    : com.st.eighteen_be.config.exception.sub_exceptions.data_exceptions
 * fileName       : NotFoundException
 * author         : ipeac
 * date           : 2024-03-27
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-03-27        ipeac       최초 생성
 */
@Getter
public class BadRequestException extends CustomeRuntimeException {
    
    public BadRequestException(ErrorCode errorCode) {
        super(errorCode);
    }
}