package com.st.eighteen_be.user.domain;

import jakarta.persistence.Embeddable;
import lombok.Getter;

@Getter
@Embeddable
public class SchoolData {

    private String schoolName;

    private String locationSchoolName;

    protected SchoolData(){}

    public SchoolData(String schoolName, String locationSchoolName) {
        this.schoolName = schoolName;
        this.locationSchoolName = locationSchoolName;
    }
}
