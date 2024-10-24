package com.st.eighteen_be.user.dto.request;

import com.st.eighteen_be.user.domain.SchoolData;
import com.st.eighteen_be.user.domain.UserInfo;
import com.st.eighteen_be.user.enums.CategoryType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

import java.time.LocalDate;

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

@Builder
public record SignUpRequestDto(

        // 전화번호
        @NotBlank(message = "공백으로 설정할 수 없습니다.")
        @NotNull(message = "전화번호는 필수 입력 값 입니다.")
        @Pattern(regexp = "^01(0|1|[6-9])[0-9]{3,4}[0-9]{4}$")
        @Schema(description = "회원가입자 전화번호", example = "01012345679")
        String phoneNumber,

        // 고유 아이디
        @NotBlank(message = "공백으로 설정할 수 없습니다.")
        @NotNull(message = "식별아이디는 필수 입력 값 입니다.")
        @Pattern(regexp = "^[A-Za-z0-9_\\-./]+$")
        @Schema(description = "회원가입자 고유식별 아이디", example = "AB")
        String uniqueId,

        // 닉네임
        @NotBlank(message = "공백으로 설정할 수 없습니다.")
        @NotNull(message = "닉네임은 필수 입력 값 입니다.")
        @Schema(description = "회원가입자 닉네임", example = "최도혁")
        String nickName,

        // 생년월일
        @NotNull(message = "생년월일은 필수 입력 값 입니다.")
        @Schema(description = "회원가입자 생년월일", example = "2024-12-23")
        LocalDate birthDay,

        // 학교
        @NotNull(message = "학교는 필수 입력 값 입니다.")
        @Schema(description = "회원가입자 학교정보",
                example = "{\"schoolName\":\"서울고등학교\", \"schoolLocation\":\"서울\"}")
        SchoolData schoolData,

        @NotNull(message = "카테고리는 필수 입니다.")
        @Schema(description = "회원가입자 카테고리", example = "기타")
        String category,

        @Schema(description = "회원가입자 토너먼트 참여 여부", example = "true")
        boolean tournamentJoin
) {

    public UserInfo toEntity(String encryptPhoneNumber, CategoryType category) {
        return UserInfo.builder()
                .phoneNumber(encryptPhoneNumber)
                .uniqueId(uniqueId)
                .nickName(nickName)
                .schoolData(schoolData)
                .birthDay(birthDay)
                .category(category)
                .tournamentJoin(tournamentJoin)
                .build();
    }
}
