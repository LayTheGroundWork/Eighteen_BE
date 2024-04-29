package com.st.eighteen_be.member.domain.dto.signIn;

import com.st.eighteen_be.member.domain.MemberPrivacy;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Getter
public class SignInResponseDto {

    private String phoneNumber;

    private String password;

    private LocalDateTime birthDay;

    private String email;

    private String certificationNumber;

    public SignInResponseDto(MemberPrivacy entity){
        this.phoneNumber = entity.getPhoneNumber();
        this.password = entity.getPassword();
        this.birthDay = entity.getBirthDay();
        this.email = entity.getEmail();
        this.certificationNumber = entity.getCertificationNumber();
    }
}
