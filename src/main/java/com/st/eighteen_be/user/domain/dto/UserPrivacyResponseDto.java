package com.st.eighteen_be.user.domain.dto;

import com.st.eighteen_be.user.domain.UserPrivacy;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class UserPrivacyResponseDto {

    private String phoneNumber;

    private String password;

    private LocalDateTime birthDay;

    private String email;

    public UserPrivacyResponseDto(UserPrivacy entity) {
        this.phoneNumber = entity.getPhoneNumber();
        this.password = entity.getPassword();
        this.birthDay = entity.getBirthDay();
        this.email = entity.getEmail();
    }
}
