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
public class UserProfiles extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserInfo user;

    private String imageKey;

    @Builder
    public UserProfiles(UserInfo user, String imageKey){
        this.imageKey = imageKey;
        setUser(user);
    }

    public void setUser(UserInfo user){
        if(this.user != null)
            this.user.getProfiles().remove(this);

        this.user = user;
        user.getProfiles().add(this);
    }
}
