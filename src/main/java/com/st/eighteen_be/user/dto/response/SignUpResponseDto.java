package com.st.eighteen_be.user.dto.response;

import com.st.eighteen_be.user.domain.UserInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Getter
@NoArgsConstructor
public class SignUpResponseDto {

    private Integer id;
    private List<String> profiles;
    private Set<String> roles;
    private String nickName;
    private String uniqueId;
    private LocalDate birthDay;
    private String location;
    private String schoolName;

    @Builder
    public SignUpResponseDto(UserInfo entity, Set<String> roles, List<String> profileKeys){
        this.id = entity.getId();
        this.profiles = profileKeys;
        this.roles = roles;
        this.nickName = entity.getNickName();
        this.uniqueId = entity.getUniqueId();
        this.birthDay = entity.getBirthDay();
        this.location = entity.getSchoolData().getSchoolLocation();
        this.schoolName = entity.getSchoolData().getSchoolName();
    }

}
