package com.st.eighteen_be.message.domain.dto;

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
        private String phoneNumber;

        @Pattern(regexp = "[0-9]{0,6}", message = "인증코드는 숫자만 입력 가능합니다.")
        private String certificationNumber;

        @Builder
        public SmsCertificationRequestDto(String phoneNumber, String certificationNumber) {
                this.phoneNumber = phoneNumber;
                this.certificationNumber = certificationNumber;
        }
}
