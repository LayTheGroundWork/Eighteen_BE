package com.st.eighteen_be.user.domain;

import com.st.eighteen_be.common.basetime.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"phoneNumber"}, name = "PHONE_NUMBER_UNIQUE")})
public class UserInfo extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", updatable = false,
            unique = true, nullable = false)
    private Integer id;

    // SNS 링크 정보 [학생(1) : 주소(N)]
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserSnsLink> snsLinks = new ArrayList<>();

    @Embedded
    private SchoolData schoolData;

    // 약관 정보 N:M

    // 회원 신고 내역 N:M -> 연관테이블 : 신고내역 테이블


    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private LocalDate birthDay;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles = new ArrayList<>();

    //private List<String> profileImg;

    @Column(length = 50, nullable = false)
    private String nickName;

    @Column(length = 50, nullable = false, unique = true)
    private String uniqueId;

    @Column(length = 100)
    private String introduction;

    @Column(length = 4)
    private String mbti;

    private int likeCount;

    // 10문 10답

    @Builder
    public UserInfo(SchoolData schoolData, String phoneNumber, LocalDate birthDay, List<String> roles, String nickName, String uniqueId, String introduction, String mbti, int likeCount) {
        this.schoolData = schoolData;
        this.phoneNumber = phoneNumber;
        this.birthDay = birthDay;
        this.roles = roles;
        this.nickName = nickName;
        this.uniqueId = uniqueId;
        this.introduction = introduction;
        this.mbti = mbti;
        this.likeCount = likeCount;
    }

    public void update(){}


}



