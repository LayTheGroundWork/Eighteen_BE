package com.st.eighteen_be.user.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserProfilePageResponseDto {

    private List<UserProfileResponseDto> users;
    private Integer totalPage;

    @Builder
    public UserProfilePageResponseDto(List<UserProfileResponseDto> profileDto, Integer totalPage) {
        this.users = profileDto;
        this.totalPage = totalPage;
    }
}
