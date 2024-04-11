package com.st.eighteen_be.member.domain.dto.signIn;

import com.st.eighteen_be.member.domain.Member;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@RequiredArgsConstructor
@Getter
public class SignInResponseDto {

    private String phoneNumber;

    private String password;

    private LocalDate birthDay;

    private String email;

    private String identification;

    public SignInResponseDto(Member entity){
        this.phoneNumber = entity.getPhoneNumber();
        this.password = entity.getPassword();
        this.birthDay = entity.getBirthDay();
        this.email = entity.getEmail();
        this.identification = entity.getIdentification();
    }
}
