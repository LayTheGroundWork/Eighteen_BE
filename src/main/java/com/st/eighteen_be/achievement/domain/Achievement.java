package com.st.eighteen_be.achievement.domain;

import com.st.eighteen_be.common.basetime.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Achievement extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer next_achievement_id;

    @Column(nullable = false, length = 500)
    private String image_url;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 1000)
    private String description;

    // private String reward_type;

    // private String reward;

    @Column(nullable = false)
    private boolean completion_goal;

    private LocalDate start_date;

    private LocalDate end_date;

    @Builder
    public Achievement(String image_url, String name, String description, boolean completion_goal, LocalDate start_date, LocalDate end_date) {
        this.image_url = image_url;
        this.name = name;
        this.description = description;
        this.completion_goal = completion_goal;
        this.start_date = start_date;
        this.end_date = end_date;
    }
}
