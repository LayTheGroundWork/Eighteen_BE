package com.st.eighteen_be.user.dto.response;

import com.st.eighteen_be.user.domain.SnsPlatform;
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
    private List<String> profileImages;
    private int likeCount;
    private String nickName;
    private String uniqueId;
    private String mbti;
    private String introduction;
    private LocalDate birthDay;
    private String location;
    private String schoolName;
    private SnsPlatform snsPlatform;
    private String category;
    private List<UserQuestionResponseDto> questions;

    @Builder
    public UserDetailsResponseDto(UserInfo entity, int likeCount, List<String> images, List<UserQuestionResponseDto> questionResponseDtoList) {
        this.id = entity.getId();
        this.profileImages = images;
        this.category = entity.getCategory().getCategory();
        this.likeCount = likeCount;
        this.nickName = entity.getNickName();
        this.uniqueId = entity.getUniqueId();
        this.mbti = entity.getMbti();
        this.introduction = entity.getIntroduction();
        this.birthDay = entity.getBirthDay();
        this.location = entity.getSchoolData().getSchoolLocation();
        this.schoolName = entity.getSchoolData().getSchoolName();
        this.snsPlatform = entity.getSnsPlatform();
        this.questions = questionResponseDtoList;
    }

}
