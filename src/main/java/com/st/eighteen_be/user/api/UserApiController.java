package com.st.eighteen_be.user.api;

import com.st.eighteen_be.common.response.ApiResponse;
import com.st.eighteen_be.common.security.SecurityUtil;
import com.st.eighteen_be.jwt.JwtRequestDto;
import com.st.eighteen_be.jwt.JwtTokenDto;
import com.st.eighteen_be.user.domain.UserPrivacy;
import com.st.eighteen_be.user.dto.sign.SignInRequestDto;
import com.st.eighteen_be.user.dto.sign.SignUpRequestDto;
import com.st.eighteen_be.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping
public class UserApiController {

    private final UserService userService;

    @PostMapping("/v1/api/user/sign-up")
    public ApiResponse<UserPrivacy> signUp(@Valid @RequestBody SignUpRequestDto requestDto){

        return ApiResponse.success(HttpStatus.OK, userService.save(requestDto));
    }

    @PostMapping("/v1/api/user/sign-in")
    public ApiResponse<JwtTokenDto> signIn(@Valid @RequestBody SignInRequestDto requestDto) {

        JwtTokenDto jwtTokenDto = userService.signIn(requestDto);

        return ApiResponse.success(HttpStatus.OK, jwtTokenDto);
    }

    @PostMapping("/v1/api/user/reissue")
    public ApiResponse<JwtTokenDto> reissue(@RequestBody JwtRequestDto requestDto) {
        return ApiResponse.success(HttpStatus.OK, userService.reissue(requestDto));
    }

    @PostMapping("/test")
    public String test() {
        return SecurityUtil.getCurrentUsername();
    }
}
