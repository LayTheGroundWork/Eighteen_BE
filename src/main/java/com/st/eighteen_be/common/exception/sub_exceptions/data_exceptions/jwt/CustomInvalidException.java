package com.st.eighteen_be.common.exception.sub_exceptions.data_exceptions.jwt;

import com.st.eighteen_be.common.exception.ErrorCode;
import com.st.eighteen_be.common.exception.base_exceptions.CustomeRuntimeException;
import lombok.Getter;

@Getter
public class CustomInvalidException extends CustomeRuntimeException {
    public CustomInvalidException(ErrorCode errorCode){super(errorCode);}
}
