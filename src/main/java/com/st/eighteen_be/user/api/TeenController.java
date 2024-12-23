package com.st.eighteen_be.user.api;

import com.st.eighteen_be.common.response.ApiResp;
import com.st.eighteen_be.user.dto.response.UserProfileResponseDto;
import com.st.eighteen_be.user.service.TeenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * packageName    : com.st.eighteen_be.user.api
 * fileName       : TeenController
 * author         : ehgur
 * date           : 2024-12-23
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-12-23        ehgur            최초 생성
 */

@Slf4j
@Tag(name = "TEEN API", description = "TEEN API")
@RestController
@RequiredArgsConstructor
public class TeenController {

    private final TeenService teenService;

    @Operation(summary = "[GUEST] 오늘의 인기 Teen 조회",
            description = "순서 랜덤하게 뿌림 / 헤더에 토큰값 필수x / 페이징 처리 / request로 page랑 size만 보내주세요")
    @GetMapping("/v1/api/guest/find-all")
    public ApiResp<List<UserProfileResponseDto>> getFamousTeenByGuest() {
        return ApiResp.success(HttpStatus.OK, teenService.getFamousTeenByGuest());
    }

    @Operation(summary = "[USER] 오늘의 인기 Teen 조회",
            description = "순서 랜덤하게 뿌림 / 헤더에 토큰값 필수 / 페이징 처리 / request로 page랑 size만 보내주세요")
    @GetMapping("/v1/api/user/find-all")
    public ApiResp<List<UserProfileResponseDto>> getFamousTeenByUser(){
        return ApiResp.success(HttpStatus.OK, teenService.getFamousTeenByUser());
    }

//    @Operation(summary = "[GUEST] 카테고리에 맞는 오늘의 인기 Teen 조회",
//            description = "")
//    @GetMapping("/v1/api/guest/find-all-by-category/{category}")
//    public ApiResp<UserProfilePageResponseDto> findAllByCategory(@Parameter(description = "카테고리", required = true)
//                                                                 @PathVariable("category") CategoryType category,
//                                                                 @PageableDefault(page = 0, size = 10) Pageable pageable){
//        return ApiResp.success(HttpStatus.OK, userDtoService.getUserProfilesWithCategory(category,pageable));
//    }
//
//    @Operation(summary = "[USER] 카테고리에 맞는 오늘의 인기 Teen 조회",
//            description = "")
//    @GetMapping("/v1/api/user/find-all-by-category/{category}")
//    public ApiResp<UserProfilePageResponseDto> findAllByCategory(@AuthenticationPrincipal UserDetails userDetails,
//                                                                 @PageableDefault(page = 0, size = 10) Pageable pageable,
//                                                                 @Parameter(description = "카테고리", required = true)
//                                                                 @PathVariable("category") CategoryType category) {
//        return ApiResp.success(HttpStatus.OK, userDtoService.getUserProfilesWithLikeStatusAndCategory(
//                userDetails.getUsername(),category,pageable));
//    }

}
