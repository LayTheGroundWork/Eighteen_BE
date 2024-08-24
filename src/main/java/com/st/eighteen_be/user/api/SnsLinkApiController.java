package com.st.eighteen_be.user.api;

import com.st.eighteen_be.common.response.ApiResp;
import com.st.eighteen_be.user.service.SnsLinkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(name = "소셜링크 API", description = "소셜링크 API")
@RestController
@RequiredArgsConstructor
@RequestMapping
public class SnsLinkApiController {

    private final SnsLinkService snsLinkService;

    @Operation(summary = "회원 sns 링크 추가", description = "회원 sns 링크 추가")
    @PostMapping("/v1/api/user/sns-links/save")
    public ApiResp<List<String>> save_snsLink(@RequestHeader("Authorization") String accessToken,
                                                           @RequestBody List<String> links){

        return ApiResp.success(HttpStatus.OK, snsLinkService.addSnsLink(accessToken,links));
    }

    @Operation(summary = "회원 sns 링크 조회", description = "회원 sns 링크 조회")
    @PostMapping("/v1/api/user/sns-link/read/{user-id}")
    public ApiResp<List<String>> readAllSnsLink(@PathVariable("user-id") Integer userId){
        return ApiResp.success(HttpStatus.OK,snsLinkService.readAll(userId));
    }
}
