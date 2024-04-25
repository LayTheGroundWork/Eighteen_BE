package com.st.eighteen_be.member.domain;

import com.st.eighteen_be.common.basetime.BaseEntity;
import com.st.eighteen_be.member.enums.GradeType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class MemberProfile extends BaseEntity {

    @Id
    private Integer id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private MemberPrivacy memberPrivacy;

    // s3 주소
    private String profileImg;

    @Column(length = 50, unique = true)
    private String nickName;

    // 학년 데이터는 수정될 가능성이 현저히 낮기 때문에 Convert를 사용하지 않음
    private GradeType grade;

    @Column(length = 300)
    private String introduction;

    @Column(length = 4)
    private String mbti;

    @Column(nullable = false)
    private Boolean gender;

    // SNS 링크 정보 [학생(1) : 주소(N)]
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberSnsLink> snsLinks = new ArrayList<>();


    // 학교 위치 정보 [학교(1) : 학생(N)]



    // 약관 정보 N:M

    // 회원 신고 내역 N:M -> 연관테이블 : 신고내역 테이블



    @Builder
    private MemberProfile(MemberPrivacy memberPrivacy, String profileImg, String nickName, GradeType grade, String introduction, String mbti, Boolean gender){

        this.memberPrivacy = memberPrivacy;
        this.profileImg = profileImg;
        this.nickName = nickName;
        this.grade = grade;
        this.introduction = introduction;
        this.mbti = mbti;
        this.gender = gender;
    }

    public MemberProfile update(String profileImg, String nickName, GradeType grade, String introduction, String mbti){
        this.profileImg = profileImg;
        this.nickName = nickName;
        this.grade = grade;
        this.introduction = introduction;
        this.mbti = mbti;

        return this;
    }

}
