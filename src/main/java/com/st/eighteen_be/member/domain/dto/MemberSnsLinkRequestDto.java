package com.st.eighteen_be.member.domain.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class MemberSnsLinkRequestDto {

    private String snsLink;

    @Builder
    private MemberSnsLinkRequestDto(String snsLink) {
        this.snsLink = snsLink;
    }
}
