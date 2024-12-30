package com.st.eighteen_be.user.api;

import com.st.eighteen_be.common.response.ApiResp;
import com.st.eighteen_be.user.dto.response.UserProfileResponseDto;
import com.st.eighteen_be.user.enums.CategoryType;
import com.st.eighteen_be.user.service.TeenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @Operation(summary = "[GUEST] 카테고리에 맞는 오늘의 인기 Teen 조회")
    @GetMapping("/v1/api/teen/guest/famous/{category}")
    public ApiResp<List<UserProfileResponseDto>> findFamousTeenByGuestAndCategory(@Parameter(description = "카테고리", required = true)
                                                                                  @PathVariable("category") CategoryType category) {
        return ApiResp.success(HttpStatus.OK, teenService.getFamousTeenByGuestWithCategory(category));
    }

    @Operation(summary = "[USER] 카테고리에 맞는 오늘의 인기 Teen 조회")
    @GetMapping("/v1/api/teen/user/famous/{category}")
    public ApiResp<List<UserProfileResponseDto>> findFamousTeenByUserAndCategory(@AuthenticationPrincipal UserDetails userDetails,
                                                                                 @Parameter(description = "카테고리", required = true)
                                                                                 @PathVariable("category") CategoryType category) {
        return ApiResp.success(HttpStatus.OK, teenService.getFamousTeenByUserWithCategory(userDetails.getUsername(), category));
    }

}
