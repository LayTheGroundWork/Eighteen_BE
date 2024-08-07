package com.st.eighteen_be.user.domain;

import com.st.eighteen_be.common.basetime.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    // TODO: snsLink 최대 3개로 제한이라 따로 테이블 만들 필요 없을 듯
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

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserRoles> roles = new HashSet<>();

    //private List<String> profileImg;

    @Column(length = 50, nullable = false)
    private String nickName;

    @Column(length = 50, nullable = false, unique = true)
    private String uniqueId;

    @Column(length = 100)
    private String introduction;

    @Column(length = 4)
    private String mbti;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserLike> userLikes = new ArrayList<>();

    private int likeCount;

    // 10문 10답

    @Builder
    public UserInfo(SchoolData schoolData, String phoneNumber, LocalDate birthDay, String nickName, String uniqueId, String introduction, String mbti, int likeCount) {
        this.schoolData = schoolData;
        this.phoneNumber = phoneNumber;
        this.birthDay = birthDay;
        this.nickName = nickName;
        this.uniqueId = uniqueId;
        this.introduction = introduction;
        this.mbti = mbti;
        this.likeCount = likeCount;
    }

    public void addRole(UserRoles userRole) {
        this.roles.add(userRole);
        userRole.setUser(this);
    }

    public void update(){}

    public void backupLikeCount(int likeCount){
        this.likeCount = likeCount;
    }

}



