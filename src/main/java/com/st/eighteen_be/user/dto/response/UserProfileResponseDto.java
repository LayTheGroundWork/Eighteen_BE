package com.st.eighteen_be.user.dto.response;

import com.st.eighteen_be.user.domain.UserInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserProfileResponseDto {

    //private List<String> profileImages;
    private Integer id;
    private String nickName;
    private String uniqueId;
    private String location;
    private String schoolName;
    private boolean likeStatus; // 좋아요 여부 필드 추가

    @Builder
    public UserProfileResponseDto(UserInfo entity, boolean likeStatus) {
        this.id = entity.getId();
        //this.profileImages = entity.getProfileImg();
        this.nickName = entity.getNickName();
        this.uniqueId = entity.getUniqueId();
        this.location = entity.getSchoolData().getSchoolLocation();
        this.schoolName = entity.getSchoolData().getSchoolName();
        this.likeStatus = likeStatus; // 좋아요 여부 초기화
    }
}
