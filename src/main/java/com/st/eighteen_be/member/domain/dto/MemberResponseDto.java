package com.st.eighteen_be.member.domain.dto;

import com.st.eighteen_be.member.enums.GradeType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class MemberResponseDto {

    private String profileImg;

    private String nickName;

    private GradeType grade;

    private String introduction;

    private String mbti;

    private Boolean gender;
}
