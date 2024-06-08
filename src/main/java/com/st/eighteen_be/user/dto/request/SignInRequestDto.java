package com.st.eighteen_be.user.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Builder
public record SignInRequestDto (

        @NotNull(message = "전화번호는 필수 입력 값 입니다.")
        @Pattern(regexp = "^01(0|1|[6-9])[0-9]{3,4}[0-9]{4}$")
        String phoneNumber,

        @NotNull(message = "인증번호는 필수 입력 값 입니다.")
        @Pattern(regexp = "^\\d{6}$")
        String verificationCode
) {





}
