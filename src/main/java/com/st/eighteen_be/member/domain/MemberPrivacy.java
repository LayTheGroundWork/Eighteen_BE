package com.st.eighteen_be.member.domain;

import com.st.eighteen_be.common.basetime.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;


@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "member", uniqueConstraints = {@UniqueConstraint(columnNames = {"phoneNumber"}, name = "PHONE_NUMBER_UNIQUE")})

public class MemberPrivacy extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Integer id;

    private String phoneNumber;

    private String password;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime birthDay;

    private String certificationNumber;

    private String email;

    @Builder
    private MemberPrivacy(String phoneNumber, String password, LocalDateTime birthDay, String certificationNumber, String email) {
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.birthDay = birthDay;
        this.certificationNumber =certificationNumber;
        this.email = email;
    }
}
