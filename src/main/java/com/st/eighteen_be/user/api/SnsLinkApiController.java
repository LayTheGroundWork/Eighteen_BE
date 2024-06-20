package com.st.eighteen_be.user.api;

import com.st.eighteen_be.common.response.ApiResp;
import com.st.eighteen_be.user.dto.response.SnsLinksResponseDto;
import com.st.eighteen_be.user.service.SnsLinkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(name = "유저 API", description = "유저 API")
@RestController
@RequiredArgsConstructor
@RequestMapping
public class SnsLinkApiController {

    private final SnsLinkService snsLinkService;

    @Operation(summary = "회원 sns 링크 추가", description = "회원 sns 링크 추가")
    @PostMapping("/v1/api/user/sns-links/save")
    public ApiResp<List<SnsLinksResponseDto>> save_snsLink(HttpServletRequest request,
                                                           @RequestBody List<String> links){

        return ApiResp.success(HttpStatus.OK, snsLinkService.addSnsLink(request,links));
    }

    @Operation(summary = "회원 sns 링크 조회", description = "회원 sns 링크 조회")
    @PostMapping("/v1/api/user/sns-link/read/{user-id}")
    public ApiResp<List<SnsLinksResponseDto>> readAllSnsLink(@PathVariable("user-id") Integer userId){
        return ApiResp.success(HttpStatus.OK,snsLinkService.readAll(userId));
    }
}
