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
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "마이페이지 API", description = "마이페이지 API")
@RestController
@RequiredArgsConstructor
@RequestMapping
public class MyPageApiController {

    private final MyPageService myPageService;

    @Operation(summary = "myPage 보기", description = "myPage 보기")
    @GetMapping("/v1/api/my-page")
    public ApiResp<UserDetailsResponseDto> view(@RequestHeader("Authorization") String accessToken){
        return ApiResp.success(HttpStatus.OK, myPageService.view(accessToken));
    }

    @Operation(summary = "myPage 수정", description = "myPage 수정")
    @PostMapping("/v1/api/my-page/update")
    public ApiResp<String> update(@RequestHeader("Authorization") String accessToken,
                                  @RequestBody MyPageRequestDto request) {
        myPageService.update(request,accessToken);
        return ApiResp.success(HttpStatus.OK, "수정되었습니다.");
    }

    @Operation(summary = "대표 이미지 수정", description = "대표 이미지 수정")
    @PostMapping("/v1/api/my-page/main-image-upadte")
    public ApiResp<String> mainImageUpdate(@RequestHeader("Authorization") String accessToken,
                                           @RequestBody UserMediaData userMediaData){
        myPageService.thumbnailUpdate(accessToken,userMediaData);
        return ApiResp.success(HttpStatus.OK, "대표이미지 설정 완료");
    }
}
