package com.st.eighteen_be.user.dto.response;

import com.st.eighteen_be.user.domain.UserInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
public class UserDetailsResponseDto {

    private Integer id;
    private List<String> profileImages;
    private int likeCount;
    private String nickName;
    private String uniqueId;
    private LocalDate birthDay;
    private String location;
    private String schoolName;
    private List<String> snsLinks;
    private String category;
    private Map<String,String> question;

    @Builder
    public UserDetailsResponseDto(UserInfo entity, int likeCount, List<String> images, List<String> snsLinks, Map<String,String> question) {
        this.id = entity.getId();
        this.profileImages = images;
        this.category = entity.getCategory().getCategory();
        this.likeCount = likeCount;
        this.nickName = entity.getNickName();
        this.uniqueId = entity.getUniqueId();
        this.birthDay = entity.getBirthDay();
        this.location = entity.getSchoolData().getSchoolLocation();
        this.schoolName = entity.getSchoolData().getSchoolName();
        this.snsLinks = snsLinks;
        this.question = question;
    }

}
