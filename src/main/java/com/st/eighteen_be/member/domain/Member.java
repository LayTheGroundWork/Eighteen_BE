package com.st.eighteen_be.member.domain;

import com.st.eighteen_be.common.base.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Member extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Integer id;

    private String phoneNumber;

    private String password;

    private LocalDate birthDay;

    private String identification;

    private String email;

    @Builder
    private Member(String phoneNumber, String password, LocalDate birthDay, String identification, String email) {
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.birthDay = birthDay;
        this.identification =identification;
        this.email = email;
    }
}
