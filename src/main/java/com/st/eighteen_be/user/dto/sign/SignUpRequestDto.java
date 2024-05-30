package com.st.eighteen_be.user.dto.sign;

import com.st.eighteen_be.user.domain.UserPrivacy;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
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


        // 고유 아이디
//        @NotBlank(message = "공백으로 설정할 수 없습니다.")
//        @NotNull(message = "식별아이디는 필수 입력 값 입니다.")
//        String identifierId,

        @NotNull(message = "생년월일은 필수 입력 값 입니다.")
        LocalDateTime birthDay

        // 학교
//        Sc location

        // 이미지 파일
) {

    public UserPrivacy toEntity(String encryptPhoneNumber) {

        List<String> roles = new ArrayList<>();
        roles.add("USER");

        return UserPrivacy.builder()
                .phoneNumber(encryptPhoneNumber)
                .birthDay(birthDay)
                .roles(roles)
                .build();
    }
}
