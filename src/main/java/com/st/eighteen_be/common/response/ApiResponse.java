package com.st.eighteen_be.common.response;

import com.st.eighteen_be.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

/**
 * packageName    : com.st.eighteen_be.common.response
 * fileName       : ApiResponse
 * author         : ipeac
 * date           : 24. 4. 12.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 4. 12.        ipeac       최초 생성
 */
public record ApiResponse<T>(HttpStatus status, T data, String message) {
    
    public static <T> ApiResponse<T> success(HttpStatus status, T data) {
        return new ApiResponse<>(status, data, null);
    }
    
    public static <T> ApiResponse<T> fail(ErrorCode errorCode) {
        return new ApiResponse<>(errorCode.getStatus(), null, errorCode.getMessage());
    }
}