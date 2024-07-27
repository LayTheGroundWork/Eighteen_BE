package com.st.eighteen_be.user.dto.response;

import com.st.eighteen_be.user.domain.UserInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
public class UserDetailsResponseDto {

    private Integer id;
    //private List<String> profileImages;
    private int likeCount;
    private String nickName;
    private String uniqueId;
    private LocalDate birthDay;
    private String location;
    private String schoolName;
    private List<String> roles;
    private List<String> question;

    @Builder
    public UserDetailsResponseDto(UserInfo entity, int likeCount) {
        this.id = entity.getId();
        //this.profileImages = entity.getProfileImg();
        this.likeCount = likeCount;
        this.nickName = entity.getNickName();
        this.uniqueId = entity.getUniqueId();
        this.birthDay = entity.getBirthDay();
        this.location = entity.getSchoolData().getSchoolLocation();
        this.schoolName = entity.getSchoolData().getSchoolName();
        this.roles = entity.getRoles();
        //this.question = entity.getQuestion();
    }

}
