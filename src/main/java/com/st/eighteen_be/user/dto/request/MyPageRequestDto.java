package com.st.eighteen_be.user.dto.request;

import com.st.eighteen_be.user.domain.SchoolData;
import com.st.eighteen_be.user.domain.SnsPlatform;
import com.st.eighteen_be.user.domain.UserQuestion;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MyPageRequestDto {

    @Schema(description = "회원 프로필")
    private List<String> mediaUrl;

    @Schema(description = "회원 닉네임")
    private String nickName;

    @Schema(description = "회원 학교 정보")
    private SchoolData schoolData;

    @Schema(description = "회원 소셜 플랫폼 아이디",
            example = "{\"instagram\":\"@test\", \"x\":\"test(x)\", \"tiktok\":\"test(tiktok)\", \"youtube\":\"user-test-@\"}")
    private SnsPlatform snsPlatform;

    @Schema(description = "회원 mbti", example = "infp")
    private String mbti;

    @Schema(description = "자기 소개글", example = "저는 지금 배고파요..")
    private String introduction;

    // example 수정해야함
    @Schema(description = "회원 QnA 리스트",
            example = "[{\"question\":\"ONE\",\"answer\":\"First answer\"}, {\"question\":\"FIVE\", \"answer\":\"Fifth answer\"}]"
    )
    private List<UserQuestion> questions;

    @Builder
    public MyPageRequestDto(List<String> mediaUrl, String nickName, SchoolData schoolData , SnsPlatform snsPlatform, String mbti,
                            String introduction, List<UserQuestion> questions) {
        this.mediaUrl = mediaUrl;
        this.nickName = nickName;
        this.schoolData = schoolData;
        this.snsPlatform = snsPlatform;
        this.mbti = mbti;
        this.introduction = introduction;
        this.questions = questions;
    }
}
