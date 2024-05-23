package com.st.eighteen_be.user.dto;

import com.st.eighteen_be.user.domain.UserPrivacy;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class UserPrivacyResponseDto {

    private String phoneNumber;

    private LocalDateTime birthDay;

    public UserPrivacyResponseDto(UserPrivacy entity) {
        this.phoneNumber = entity.getPhoneNumber();
        this.birthDay = entity.getBirthDay();
    }
}
