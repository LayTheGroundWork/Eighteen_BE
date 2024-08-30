package com.st.eighteen_be.message.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SmsCertificationRequestDto {

        @NotBlank(message = "공백으로 설정할 수 없습니다.")
        @NotNull(message = "전화번호는 필수 입력 값 입니다.")
        @Pattern(regexp = "^01(0|1|[6-9])[0-9]{3,4}[0-9]{4}$")
        @Schema(description = "메시지 수신자 전화번호", example = "01012345678")
        private String phoneNumber;

        @NotNull(message = "인증번호는 필수 입력 값 입니다.")
        @Pattern(regexp = "^\\d{6}$")
        @Schema(description = "로그인 유저가 적은 인증번호", example = "123456")
        private String verificationCode;

        @Builder
        public SmsCertificationRequestDto(String phoneNumber, String verificationCode) {
                this.phoneNumber = phoneNumber;
                this.verificationCode = verificationCode;
        }
}
