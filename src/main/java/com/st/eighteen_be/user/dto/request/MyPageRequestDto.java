package com.st.eighteen_be.user.dto.request;

import com.st.eighteen_be.user.domain.UserQuestion;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class MyPageRequestDto {


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
    public MyPageRequestDto(String mbti, String introduction, List<UserQuestion> questions) {
        this.mbti = mbti;
        this.introduction = introduction;
        this.questions = questions;
    }
}
