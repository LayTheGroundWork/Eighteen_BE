package com.st.eighteen_be.user.dto.response;

import com.st.eighteen_be.user.domain.UserInfo;
import com.st.eighteen_be.user.domain.UserSnsLink;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
public class UserDetailsResponseDto {


    //private List<String> profileImages;
    private int likeCount;
    private String nickName;
    private String uniqueId;
    private LocalDate birthDay;
    //private String location;
    //private String schoolName;
    private List<String> roles;
    private List<String> question;
    private List<UserSnsLink> snsLinks;

    @Builder
    public UserDetailsResponseDto(UserInfo entity) {
        //this.profileImages = entity.getProfileImg();
        this.likeCount = entity.getLikeCount();
        this.nickName = entity.getNickName();
        this.uniqueId = entity.getUniqueId();
        this.birthDay = entity.getBirthDay();
        //this.location = entity.getLocation();
        //this.schoolName = entity.getSchoolName();
        this.roles = entity.getRoles();
        //this.question = entity.getQuestion();
        this.snsLinks = entity.getSnsLinks();
    }

}
