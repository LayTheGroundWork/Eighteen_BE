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

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "roles_id")
    private Integer id;

    @Enumerated(EnumType.STRING)
    private RolesType role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserInfo user;

    @Builder
    public UserRoles(RolesType role) {
        this.role = role;
    }

    //== 연관 관계 메서드 ==//
    public void setUser(UserInfo user) {
        if (this.user != null) {
            this.user.getRoles().remove(this);
        }
        this.user = user;
        user.addRole(this);
    }
}
