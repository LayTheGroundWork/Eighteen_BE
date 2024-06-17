package com.st.eighteen_be.school.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SchoolsResponseDto {
    @JsonProperty("SCHUL_NM")
    @Schema(description = "학교 이름", example = "에이틴고등학교")
    private String schoolName;

    @JsonProperty("ORG_RDNMA")
    @Schema(description = "학교 도로명 주소", example = "서울")
    private String roadAddress;

    @Builder
    public SchoolsResponseDto(String schoolName, String roadAddress) {
        this.schoolName = schoolName;
        this.roadAddress = roadAddress;
    }

}
