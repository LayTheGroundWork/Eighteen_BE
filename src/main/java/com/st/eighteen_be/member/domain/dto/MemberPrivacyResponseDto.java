package com.st.eighteen_be.member.domain.dto;

import com.st.eighteen_be.member.domain.MemberPrivacy;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class MemberPrivacyResponseDto {

    private String phoneNumber;

    private String password;

    private LocalDateTime birthDay;

    private String certificationNumber;

    private String email;

    public MemberPrivacyResponseDto(MemberPrivacy entity) {
        this.phoneNumber = entity.getPhoneNumber();
        this.password = entity.getPassword();
        this.birthDay = entity.getBirthDay();
        this.certificationNumber = entity.getCertificationNumber();
        this.email = entity.getEmail();
    }
}
