package com.st.eighteen_be.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Builder
public record SignInRequestDto (

        @NotNull(message = "전화번호는 필수 입력 값 입니다.")
        @Pattern(regexp = "^01(0|1|[6-9])[0-9]{3,4}[0-9]{4}$")
        @Schema(description = "로그인 유저 전화번호", example = "01012345678")
        String phoneNumber,

        @NotNull(message = "인증번호는 필수 입력 값 입니다.")
        @Pattern(regexp = "^\\d{6}$")
        @Schema(description = "로그인 유저가 적은 인증번호", example = "123456")
        String verificationCode
) {
}
