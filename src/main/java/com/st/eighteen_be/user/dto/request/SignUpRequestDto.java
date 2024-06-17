package com.st.eighteen_be.user.dto.request;

import com.st.eighteen_be.user.domain.SchoolData;
import com.st.eighteen_be.user.domain.UserInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
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
@Builder
public record SignUpRequestDto(

        @NotBlank(message = "공백으로 설정할 수 없습니다.")
        @NotNull(message = "전화번호는 필수 입력 값 입니다.")
        @Pattern(regexp = "^01(0|1|[6-9])[0-9]{3,4}[0-9]{4}$")
        @Schema(description = "회원가입자 전화번호", example = "01012345678")
        String phoneNumber,

        // 고유 아이디
        @NotBlank(message = "공백으로 설정할 수 없습니다.")
        @NotNull(message = "식별아이디는 필수 입력 값 입니다.")
        @Schema(description = "회원가입자 고유식별 아이디", example = "@AB_D")
        String uniqueId,

        @NotNull(message = "생년월일은 필수 입력 값 입니다.")
        @Schema(description = "회원가입자 생년월일", example = "2024-12-23T11:00")
        LocalDateTime birthDay,

        // 학교
        @NotNull(message = "학교는 필수 입력 값 입니다.")
        @Schema(description = "회원가입자 학교정보", example = "{학교이름, 학교위치}")
        SchoolData schoolData
) {

    public UserInfo toEntity(String encryptPhoneNumber) {

        List<String> roles = new ArrayList<>();
        roles.add("USER");

        return UserInfo.builder()
                .phoneNumber(encryptPhoneNumber)
                .uniqueId(uniqueId)
                .schoolData(schoolData)
                .birthDay(birthDay)
                .roles(roles)
                .build();
    }
}
