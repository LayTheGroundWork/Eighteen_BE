package com.st.eighteen_be.user.domain;

import com.st.eighteen_be.common.basetime.BaseEntity;
import com.st.eighteen_be.common.exception.ErrorCode;
import com.st.eighteen_be.common.exception.sub_exceptions.data_exceptions.BadRequestException;
import com.st.eighteen_be.user.dto.request.MyPageRequestDto;
import com.st.eighteen_be.user.enums.CategoryType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"phoneNumber"}, name = "PHONE_NUMBER_UNIQUE")})
public class UserInfo extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", updatable = false, unique = true, nullable = false)
    private Integer id;

    @Embedded
    private SnsPlatform snsPlatform;

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
    private List<UserMediaData> mediaDataList = new ArrayList<>();

    private String thumbnail;

    @Column(length = 50, nullable = false)
    private String nickName;

    @Column(length = 50, nullable = false, unique = true)
    private String uniqueId;

    @Column(length = 100)
    private String introduction;

    @Column(length = 4)
    private String mbti;

    @ColumnDefault("true")
    private boolean tournamentJoin;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserLike> userLikes = new ArrayList<>();

    // 10문 10답
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserQuestion> userQuestions = new ArrayList<>();

    @ColumnDefault("0")
    private int likeCount;

    // 약관 정보 N:M

    // 회원 신고 내역 N:M -> 연관테이블 : 신고내역 테이블

    @Builder
    public UserInfo(SchoolData schoolData, SnsPlatform snsPlatform, CategoryType category, String thumbnail,
                    String phoneNumber, LocalDate birthDay, String nickName, String uniqueId, String introduction,
                    String mbti, int likeCount, boolean tournamentJoin) {
        this.schoolData = schoolData;
        this.snsPlatform = snsPlatform;
        this.category = category;
        this.thumbnail = thumbnail;
        this.phoneNumber = phoneNumber;
        this.birthDay = birthDay;
        this.nickName = nickName;
        this.uniqueId = uniqueId;
        this.introduction = introduction;
        this.mbti = mbti;
        this.likeCount = likeCount;
        this.tournamentJoin = tournamentJoin;
    }

    public void addQuestionAndAnswer(UserQuestion userQuestion) {
        userQuestions.add(userQuestion);
    }

    public void thumbnailUpdate(UserMediaData userMediaData) {
        this.thumbnail = userMediaData.getImageKey();
    }

    public void myPageUpdate(MyPageRequestDto requestDto){
        this.nickName = requestDto.getNickName();
        this.schoolData = requestDto.getSchoolData();
        this.snsPlatform = requestDto.getSnsPlatform();
        this.mbti = requestDto.getMbti();
        this.introduction = requestDto.getIntroduction();

        this.userQuestions.clear();  // 기존 엔티티를 삭제하여 orphanRemoval이 작동하도록 함
        for (UserQuestion question : requestDto.getQuestions()) {
            question.setUser(this);
        }
    }

    public void backupLikeCount(int likeCount){
        this.likeCount = likeCount;
    }


    public boolean isMainProfileIsDefaultImage() {
        return getMainProfile().isDefaultImage();
    }

    private UserMediaData getMainProfile() {
        if (mediaDataList.isEmpty()) {
            throw new BadRequestException(ErrorCode.NOT_FOUND_PROFILE);
        }

        return mediaDataList.get(0);
    }
}
