package com.st.eighteen_be.user.domain;

import com.st.eighteen_be.achievement.domain.Achievement;
import com.st.eighteen_be.common.basetime.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
public class UserAchievement extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn
    private UserInfo userInfo;

    @ManyToOne
    @JoinColumn
    private Achievement achievement;

    private LocalDate obtained_date;

    @Column(nullable = false)
    @ColumnDefault("0")
    private Integer current_value;

    @Column(nullable = false)
    @ColumnDefault("false")
    private boolean is_received;
}
