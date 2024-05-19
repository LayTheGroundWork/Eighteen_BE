package com.st.eighteen_be.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class UserSnsLinkRequestDto {

    private String snsLink;

    @Builder
    private UserSnsLinkRequestDto(String snsLink) {
        this.snsLink = snsLink;
    }
}
