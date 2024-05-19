package com.st.eighteen_be.common.exception.sub_exceptions.data_exceptions;

import com.st.eighteen_be.common.exception.ErrorCode;
import com.st.eighteen_be.common.exception.base_exceptions.CustomeRuntimeException;
import lombok.Getter;

@Getter
public class TokenNotFoundException extends CustomeRuntimeException {

    public TokenNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
