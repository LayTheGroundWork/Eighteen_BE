package com.st.eighteen_be.config.advice;

import com.st.eighteen_be.config.exception.ApiError;
import com.st.eighteen_be.config.exception.base_exceptions.CustomeRuntimeException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

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
    public final ResponseEntity<Object> notFoundExceptionHandler(CustomeRuntimeException e, WebRequest request) {
        ApiError apiError = new ApiError(
                e.getErrorCode().getStatus(),
                e.getErrorCode().getMessage(),
                e.getMessage(),
                request.getDescription(false),
                LocalDateTime.now()
        );
        
        return new ResponseEntity<>(apiError, e.getErrorCode().getStatus());
    }
}