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
public class SchoolData extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "school_data_id")
    private Integer id;

    private String schoolName;

    private String locationSchoolName;

    @Builder
    public SchoolData(String schoolName, String locationSchoolName) {
        this.schoolName = schoolName;
        this.locationSchoolName = locationSchoolName;
    }

    public void update(String schoolName, String locationSchoolName) {
        this.schoolName = schoolName;
        this.locationSchoolName = locationSchoolName;
    }
}
