package com.st.eighteen_be.user.domain.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@RequiredArgsConstructor
@Getter
public class UserPrivacyRequestDto {

    private String phoneNumber;

    private String password;

    private LocalDate birthDay;

    private String name;

    @Builder
    private UserPrivacyRequestDto(String phoneNumber, String password, LocalDate birthDay, String name) {

        this.phoneNumber = phoneNumber;
        this.password = password;
        this.birthDay = birthDay;
        this.name = name;
    }
}
