package com.st.eighteen_be.user.api;

import com.st.eighteen_be.common.response.ApiResponse;
import com.st.eighteen_be.common.security.SecurityUtil;
import com.st.eighteen_be.jwt.JwtTokenDto;
import com.st.eighteen_be.user.domain.UserInfo;
import com.st.eighteen_be.user.dto.sign.SignInRequestDto;
import com.st.eighteen_be.user.dto.sign.SignUpRequestDto;
import com.st.eighteen_be.user.service.UserService;
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
@RestController
@RequiredArgsConstructor
@RequestMapping
public class UserApiController {

    private final UserService userService;

    @PostMapping("/v1/api/user/sign-up")
    public ApiResponse<UserInfo> signUp(@Valid @RequestBody SignUpRequestDto requestDto){

        return ApiResponse.success(HttpStatus.OK, userService.save(requestDto));
    }

    @PostMapping("/v1/api/user/sign-in")
    public ApiResponse<JwtTokenDto> signIn(@Valid @RequestBody SignInRequestDto requestDto) {

        JwtTokenDto jwtTokenDto = userService.signIn(requestDto);

        return ApiResponse.success(HttpStatus.OK, jwtTokenDto);
    }

    @DeleteMapping("/v1/api/user/sign-out")
    public ApiResponse<String> signOut(HttpServletRequest request) {
        userService.signOut(request);
        return ApiResponse.success(HttpStatus.OK, "로그아웃 되었습니다.");
    }

    @PutMapping("/v1/api/user/reissue")
    public ApiResponse<JwtTokenDto> reissue(HttpServletRequest request) {
        return ApiResponse.success(HttpStatus.OK, userService.reissue(request));
    }

    @PostMapping("/test")
    public String test(HttpServletRequest request) {
        String accessToken = request.getHeader("Authorization");
        String refreshToken = request.getHeader("Refresh");
        return SecurityUtil.getCurrentUsername() + "\n" + accessToken + "\n" + refreshToken;
    }
}
