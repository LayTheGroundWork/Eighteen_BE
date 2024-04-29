package com.st.eighteen_be.common.exception.sub_exceptions.data_exceptions;

import com.st.eighteen_be.common.exception.ErrorCode;
import com.st.eighteen_be.common.exception.base_exceptions.CustomeRuntimeException;
import lombok.Getter;

/**
 * packageName    : com.st.eighteen_be.config.exception.sub_exceptions.data_exceptions
 * fileName       : OccupiedPhoneNumberException
 * author         : ehgur
 * date           : 2024-04-26
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-04-26        ehgur            최초 생성
 */

@Getter
public class OccupiedException extends CustomeRuntimeException {

    public OccupiedException(ErrorCode errorCode){ super(errorCode); }
}
