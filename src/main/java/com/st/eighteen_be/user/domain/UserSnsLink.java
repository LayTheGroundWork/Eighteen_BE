package com.st.eighteen_be.user.domain;

import com.st.eighteen_be.common.basetime.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserSnsLink extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sns_link_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserInfo user;

    private String link;

    @Builder
    public UserSnsLink(UserInfo user, String link) {
        this.user = user;
        this.link = link;
    }

    //== 생성 메서드 ==//
    public static UserSnsLink addUserSnsLink(UserInfo user, String link) {
        UserSnsLink userSnsLink = UserSnsLink.builder()
                .user(user)
                .link(link)
                .build();

        user.getSnsLinks().add(userSnsLink);

        return userSnsLink;
    }

}
