package com.st.eighteen_be.user.dto.response;

import com.st.eighteen_be.user.domain.UserInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserProfileResponseDto {

    //private List<String> profileImages;
    private String nickName;
    private String uniqueId;
    //private String location;
    //private String schoolName;

    @Builder
    public UserProfileResponseDto(UserInfo entity) {
        //this.profileImages = entity.getProfileImg();
        this.nickName = entity.getNickName();
        this.uniqueId = entity.getUniqueId();
        //this.location = entity.getLocation();
        //this.schoolName = entity.getSchoolName();
    }
}
