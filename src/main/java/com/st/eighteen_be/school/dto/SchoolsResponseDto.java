package com.st.eighteen_be.school.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SchoolsResponseDto {
    @JsonProperty("SCHUL_NM")
    private String schoolName;

    @JsonProperty("ORG_RDNMA")
    private String roadAddress;

    @Builder
    public SchoolsResponseDto(String schoolName, String roadAddress) {
        this.schoolName = schoolName;
        this.roadAddress = roadAddress;
    }

}
