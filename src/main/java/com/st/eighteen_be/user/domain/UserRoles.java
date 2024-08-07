package com.st.eighteen_be.user.domain;

import com.st.eighteen_be.common.basetime.BaseEntity;
import com.st.eighteen_be.user.enums.RolesType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserRoles extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "roles_id")
    private Integer id;

    @Enumerated(EnumType.STRING)
    private RolesType role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserInfo user;

    @Builder
    public UserRoles(RolesType role, UserInfo user) {
        this.role = role;
        setUser(user);
    }

    //== 연관 관계 메서드 ==//
    public void setUser(UserInfo user) {
        this.user = user;
        user.getRoles().add(this);
    }
}
