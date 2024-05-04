package com.st.eighteen_be.member.domain.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record LoginRequestDto(

        @NotNull(message = "전화번호는 필수 입력 값 입니다.")
        @Pattern(regexp = "^01(0|1|[6-9])[0-9]{3,4}[0-9]{4}$")
        String phoneNumber,

        @Pattern(regexp = "^(?=.*\\d)(?=.*[a-zA-Z])[0-9a-zA-Z]{8,16}",
                message = "비밀번호는 영문과 숫자 조합으로 8 ~ 16자리까지 가능합니다.")
        String password
) {
}
