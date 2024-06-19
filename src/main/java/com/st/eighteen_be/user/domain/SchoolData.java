package com.st.eighteen_be.user.domain;

import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Getter;

@Getter
@Embeddable
public class SchoolData {

    private String schoolName;

    private String schoolLocation;

    protected SchoolData(){}

    @Builder
    public SchoolData(String schoolName, String schoolLocation) {
        this.schoolName = schoolName;
        this.schoolLocation = schoolLocation;
    }
}
