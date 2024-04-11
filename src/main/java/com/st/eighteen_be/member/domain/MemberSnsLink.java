package com.st.eighteen_be.member.domain;

import com.st.eighteen_be.common.base.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class MemberSnsLink extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sns_link_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private MemberProfile member;

    private String snsLink;

    @Builder
    private MemberSnsLink(MemberProfile member, String snsLink) {
        this.member = member;
        //== 연관관계 편의 메서드 ==//
        member.getSnsLinks().add(this);

        this.snsLink = snsLink;
    }

}
