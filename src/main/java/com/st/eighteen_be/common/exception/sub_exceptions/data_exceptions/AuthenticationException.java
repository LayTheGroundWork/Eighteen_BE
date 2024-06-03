package com.st.eighteen_be.common.exception.sub_exceptions.data_exceptions;

import com.st.eighteen_be.common.exception.ErrorCode;
import com.st.eighteen_be.common.exception.base_exceptions.CustomeRuntimeException;
import lombok.Getter;

/**
 * packageName    : com.st.eighteen_be.common.exception.sub_exceptions.data_exceptions
 * fileName       : Authentication
 * author         : ehgur
 * date           : 2024-04-30
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-04-30        ehgur             최초 생성
 */

@Getter
public class AuthenticationException extends CustomeRuntimeException {

    public AuthenticationException(ErrorCode errorCode){ super(errorCode); }
}
