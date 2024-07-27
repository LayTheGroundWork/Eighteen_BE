package com.st.eighteen_be.user.domain;

import com.st.eighteen_be.common.basetime.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class UserLike extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "liker_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserInfo user;

    private Integer likedId;

    @Builder
    public UserLike(UserInfo user, Integer likedId) {
        this.user = user;
        this.likedId = likedId;
    }

    //== 생성 메서드 ==//
    public static void addLikedId(UserInfo user, Integer likedId) {
        UserLike userLike = UserLike.builder()
                .user(user)
                .likedId(likedId)
                .build();

        user.getUserLikes().add(userLike);
    }
}
