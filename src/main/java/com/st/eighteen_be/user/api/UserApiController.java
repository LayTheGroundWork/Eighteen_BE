package com.st.eighteen_be.user.api;

import com.st.eighteen_be.common.response.ApiResp;
import com.st.eighteen_be.common.security.SecurityUtil;
import com.st.eighteen_be.jwt.JwtTokenDto;
import com.st.eighteen_be.user.domain.UserInfo;
import com.st.eighteen_be.user.dto.request.SignInRequestDto;
import com.st.eighteen_be.user.dto.request.SignUpRequestDto;
import com.st.eighteen_be.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * packageName    : com.st.eighteen_be.member.api
 * fileName       : MemberApiController
 * author         : ehgur
 * date           : 2024-04-18
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-04-18        ehgur             최초 생성
 */

@Slf4j
@Tag(name = "유저 API", description = "유저 API")
@RestController
@RequiredArgsConstructor
@RequestMapping
public class UserApiController {

    private final UserService userService;

    @Operation(summary = "회원가입", description = "회원가입")
    @PostMapping("/v1/api/user/sign-up")
    public ApiResp<UserInfo> signUp(@Valid @RequestBody SignUpRequestDto requestDto){

        return ApiResp.success(HttpStatus.OK, userService.save(requestDto));
    }

    @Operation(summary = "로그인", description = "로그인")
    @PostMapping("/v1/api/user/sign-in")
    public ApiResp<JwtTokenDto> signIn(@Valid @RequestBody SignInRequestDto requestDto) {

        JwtTokenDto jwtTokenDto = userService.signIn(requestDto);

        return ApiResp.success(HttpStatus.OK, jwtTokenDto);
    }

    @Operation(summary = "로그아웃", description = "로그아웃")
    @DeleteMapping("/v1/api/user/sign-out")
    public ApiResp<String> signOut(HttpServletRequest request) {
        userService.signOut(request);
        return ApiResp.success(HttpStatus.OK, "로그아웃 되었습니다.");
    }

    @Operation(summary = "토큰 재발급", description = "토큰 재발급")
    @PutMapping("/v1/api/user/reissue")
    public ApiResp<JwtTokenDto> reissue(HttpServletRequest request) {
        return ApiResp.success(HttpStatus.OK, userService.reissue(request));
    }

    @Operation(summary = "토큰 인증 테스트", description = "유효한 토큰인지 테스트")
    @PostMapping("/test")
    public ApiResp<String> test(HttpServletRequest request) {
        String accessToken = request.getHeader("Authorization");
        String refreshToken = request.getHeader("Refresh");
        return ApiResp.success(HttpStatus.OK,
                SecurityUtil.getCurrentUsername() + "\n" + accessToken + "\n" + refreshToken);
    }
}