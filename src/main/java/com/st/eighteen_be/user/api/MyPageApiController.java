package com.st.eighteen_be.user.api;

import com.st.eighteen_be.common.response.ApiResp;
import com.st.eighteen_be.user.domain.UserMediaData;
import com.st.eighteen_be.user.dto.request.MyPageRequestDto;
import com.st.eighteen_be.user.dto.response.UserDetailsResponseDto;
import com.st.eighteen_be.user.service.MyPageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "마이 페이지 API", description = "마이 페이지 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/my-page")
public class MyPageApiController {

    private final MyPageService myPageService;

    @Operation(summary = "myPage 보기", description = "myPage 조회")
    @GetMapping
    public ApiResp<UserDetailsResponseDto> view(@AuthenticationPrincipal UserDetails userDetails) {
        return ApiResp.success(HttpStatus.OK, myPageService.view(userDetails.getUsername()));
    }

    @Operation(summary = "myPage 수정", description = "myPage 수정")
    @PutMapping
    public ApiResp<String> update(@AuthenticationPrincipal UserDetails userDetails,
                                  @RequestBody MyPageRequestDto request) {
        myPageService.update(userDetails.getUsername(), request);

        return ApiResp.success(HttpStatus.OK, "수정 완료.");
    }

    @Operation(summary = "프로필 이미지 또는 동영상 삭제", description = "프로필 이미지 또는 동영상 삭제")
    @DeleteMapping
    public ApiResp<String> delete(@AuthenticationPrincipal UserDetails userDetails,
                                  @RequestBody String imageKey) {
        myPageService.profileDelete(userDetails.getUsername(), imageKey);
        return ApiResp.success(HttpStatus.OK, "삭제 완료.");
    }


}
