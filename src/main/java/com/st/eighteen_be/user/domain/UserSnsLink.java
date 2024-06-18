package com.st.eighteen_be.user.domain;

import com.st.eighteen_be.common.basetime.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class UserSnsLink extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sns_link_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private UserInfo user;

    private String snsLink;

    @Builder
    public UserSnsLink(String snsLink) {
        this.snsLink = snsLink;
    }

    //== 연관관계 편의 메서드 ==//
    public void setUser(UserInfo user) {
        this.user = user;
        user.getSnsLinks().add(this);
    }

}
