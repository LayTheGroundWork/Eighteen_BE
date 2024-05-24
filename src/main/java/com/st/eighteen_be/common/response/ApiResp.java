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
public record ApiResp<T>(HttpStatus status, T data, String message) {

    public static <T> ApiResp<T> success(HttpStatus status, T data) {
        return new ApiResp<>(status, data, "성공");
    }

    public static <T> ApiResp<T> fail(ErrorCode errorCode) {
        return new ApiResp<>(errorCode.getStatus(), null, errorCode.getMessage());
    }
}