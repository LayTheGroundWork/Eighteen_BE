package com.st.eighteen_be.common.response;

import com.st.eighteen_be.common.exception.ErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "API 응답")
public record ApiResp<T>(
        @Schema(description = "HTTP 상태 코드", example = "200")
        int status,
        @Schema(description = "응답 데이터")
        T data,
        @Schema(description = "메시지")
        String message) {
    public static <T> ApiResp<T> success(HttpStatus status, T data) {
        return new ApiResp<>(status.value(), data, "성공");
    }
    
    public static <T> ApiResp<T> fail(ErrorCode errorCode) {
        return new ApiResp<>(errorCode.getStatus().value(), null, errorCode.getMessage());
    }
}
