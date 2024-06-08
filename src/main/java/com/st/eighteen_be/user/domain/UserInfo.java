package com.st.eighteen_be.user.domain;

import com.st.eighteen_be.common.basetime.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"phoneNumber"}, name = "PHONE_NUMBER_UNIQUE")})
public class UserInfo extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", updatable = false,
            unique = true, nullable = false)
    private Integer id;

    // SNS 링크 정보 [학생(1) : 주소(N)]
    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserSnsLink> snsLinks = new ArrayList<>();



    // 학교 위치 정보 [학교(1) : 학생(N)]

    // 약관 정보 N:M

    // 회원 신고 내역 N:M -> 연관테이블 : 신고내역 테이블


    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime birthDay;

    @ElementCollection(fetch = FetchType.EAGER)
    @Builder.Default
    private List<String> roles = new ArrayList<>();

    //private List<String> profileImg;

    @Column(length = 50)
    private String nickName;

    @Column(length = 50, nullable = false ,unique = true)
    private String uniqueId;

    @Column(length = 100)
    private String introduction;

    @Column(length = 4)
    private String mbti;

    private int likeCount;

    // 10문 10답


}



