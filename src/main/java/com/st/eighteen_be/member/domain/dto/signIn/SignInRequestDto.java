package com.st.eighteen_be.member.domain.dto.signIn;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.st.eighteen_be.member.domain.MemberPrivacy;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * packageName    : com.st.eighteen_be.service.MemberService
 * fileName       : SignInRequestDto
 * author         : ehgur
 * date           : 2024-04-18
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-04-18        ehgur             최초 생성
 */

public record SignInRequestDto(

        @NotBlank(message = "공백으로 설정할 수 없습니다.")
        @NotNull(message = "전화번호는 필수 입력 값 입니다.")
        @Pattern(regexp = "^01(0|1|[6-9])[0-9]{3,4}[0-9]{4}$")
        String phoneNumber,

        @Pattern(regexp = "[0-9]{0,6}", message = "인증코드는 숫자만 입력 가능합니다.")
        String certificationNumber,


        @NotBlank(message = "공백으로 설정할 수 없습니다.")
        @NotNull(message = "비밀번호는 필수 입력 값 입니다.")
        @Pattern(regexp = "^(?=.*\\d)(?=.*[a-zA-Z])[0-9a-zA-Z]{8,16}",
                message = "비밀번호는 영문과 숫자 조합으로 8 ~ 16자리까지 가능합니다.")
         String password,


        @NotNull(message = "생년월일은 필수 입력 값 입니다.")
        LocalDateTime birthDay,


        @NotBlank(message = "공백으로 설정할 수 없습니다.")
        @NotBlank(message = "이메일은 필수 입력 값 입니다.")
        @Email(message = "이메일 형식이 아닙니다.")
        String email
) {

    public MemberPrivacy toEntity(String encodePassword) {
        return MemberPrivacy.builder()
                .phoneNumber(phoneNumber)
                .password(encodePassword)
                .birthDay(birthDay)
                .email(email)
                .certificationNumber(certificationNumber)
                .build();
    }
}
