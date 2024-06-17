package com.st.eighteen_be.school.controller;

import com.st.eighteen_be.common.response.ApiResp;
import com.st.eighteen_be.school.dto.SchoolsResponseDto;
import com.st.eighteen_be.school.service.SchoolService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Tag(name = "학교 API", description = "학교 API")
@RestController
@RequiredArgsConstructor
@RequestMapping
public class SchoolApiController {

    private final SchoolService schoolService;

    @Operation(summary = "학교검색", description = "학교검색")
    @GetMapping("/v1/api/schools")
    public ApiResp<List<SchoolsResponseDto>> searchSchools(@RequestParam String schoolName) {
        return ApiResp.success(HttpStatus.OK, schoolService.searchSchools(schoolName).block());
    }
}
