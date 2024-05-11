package com.st.eighteen_be.config.advice;

import com.st.eighteen_be.common.exception.base_exceptions.CustomeRuntimeException;
import com.st.eighteen_be.common.response.ApiResp;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * packageName    : com.st.eighteen_be.config.exception
 * fileName       : CustomeAdvice
 * author         : ipeac
 * date           : 2024-03-27
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-03-27        ipeac       최초 생성
 */
@RestControllerAdvice
public class CustomAdvice {
    
    @ExceptionHandler(CustomeRuntimeException.class)
    public final ApiResp<Object> notFoundExceptionHandler(CustomeRuntimeException e) {
        return ApiResp.fail(e.getErrorCode());
    }
}