package com.st.eighteen_be.common.exception.sub_exceptions.data_exceptions;


import com.st.eighteen_be.common.exception.ErrorCode;
import com.st.eighteen_be.common.exception.base_exceptions.CustomeRuntimeException;

/**
 * packageName    : com.st.eighteen_be.common.exception.sub_exceptions.data_exceptions
 * fileName       : NotValidException
 * author         : ehgur
 * date           : 2024-05-27
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-05-27        ehgur            최초 생성
 */

public class NotValidException extends CustomeRuntimeException {

    public NotValidException(ErrorCode errorCode) {
        super(errorCode);
    }
}
