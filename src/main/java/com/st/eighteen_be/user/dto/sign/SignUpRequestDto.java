package com.st.eighteen_be.user.dto.sign;

import com.st.eighteen_be.user.domain.UserPrivacy;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;

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

@Slf4j
public record SignUpRequestDto(

        @NotBlank(message = "공백으로 설정할 수 없습니다.")
        @NotNull(message = "전화번호는 필수 입력 값 입니다.")
        @Pattern(regexp = "^01(0|1|[6-9])[0-9]{3,4}[0-9]{4}$")
        String phoneNumber,

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
        String email,

        List<String> roles
) {

    public UserPrivacy toEntity(String encodePassword, List<String> roles) {
        return UserPrivacy.builder()
                .phoneNumber(phoneNumber)
                .password(encodePassword)
                .birthDay(birthDay)
                .email(email)
                .roles(roles)
                .build();
    }
}
