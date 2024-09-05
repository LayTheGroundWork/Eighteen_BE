package com.st.eighteen_be.user.domain;

import com.st.eighteen_be.common.basetime.BaseEntity;
import com.st.eighteen_be.common.exception.ErrorCode;
import com.st.eighteen_be.common.exception.sub_exceptions.data_exceptions.BadRequestException;
import com.st.eighteen_be.user.dto.request.MyPageRequestDto;
import com.st.eighteen_be.user.enums.CategoryType;
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

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserSnsLink> snsLinks = new ArrayList<>();

    @Embedded
    private SchoolData schoolData;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CategoryType category;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private LocalDate birthDay;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserRoles> roles = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserProfiles> profiles = new ArrayList<>();

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

    // 10문 10답
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserQuestion> userQuestions = new ArrayList<>();

    private int likeCount;

    // 약관 정보 N:M

    // 회원 신고 내역 N:M -> 연관테이블 : 신고내역 테이블

    @Builder
    public UserInfo(SchoolData schoolData, CategoryType category, String phoneNumber, LocalDate birthDay,
                    String nickName, String uniqueId, String introduction, String mbti, int likeCount) {
        this.schoolData = schoolData;
        this.category = category;
        this.phoneNumber = phoneNumber;
        this.birthDay = birthDay;
        this.nickName = nickName;
        this.uniqueId = uniqueId;
        this.introduction = introduction;
        this.mbti = mbti;
        this.likeCount = likeCount;
    }

    public void addSnsLink(UserSnsLink snsLink) {
        this.snsLinks.add(snsLink);
        snsLink.setUser(this);
    }

    public void addRole(UserRoles userRole) {
        this.roles.add(userRole);
        userRole.setUser(this);
    }

    public void addProfile(UserProfiles userProfile){
        this.profiles.add(userProfile);
        userProfile.setUser(this);
    }

    public void addQuestionAndAnswer(UserQuestion userQuestion) {
        this.userQuestions.add(userQuestion);
        userQuestion.setUser(this);
    }

    public void update(MyPageRequestDto requestDto){
        this.mbti = requestDto.getMbti();
        this.introduction = requestDto.getIntroduction();

        this.userQuestions.clear();  // 기존 엔티티를 삭제하여 orphanRemoval이 작동하도록 함
        for (UserQuestion question : requestDto.getQuestions()) {
            addQuestionAndAnswer(question);
        }
    }

    public void backupLikeCount(int likeCount){
        this.likeCount = likeCount;
    }


    public boolean isMainProfileIsDefaultImage() {
        return getMainProfile().isDefaultImage();
    }

    private UserProfiles getMainProfile() {
        if (profiles.isEmpty()) {
            throw new BadRequestException(ErrorCode.NOT_FOUND_PROFILE);
        }

        return profiles.get(0);
    }
}
