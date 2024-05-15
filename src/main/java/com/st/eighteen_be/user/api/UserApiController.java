package com.st.eighteen_be.user.api;

import com.st.eighteen_be.common.response.ApiResponse;
import com.st.eighteen_be.jwt.Jwt;
import com.st.eighteen_be.user.domain.UserPrivacy;
import com.st.eighteen_be.user.domain.dto.LoginRequestDto;
import com.st.eighteen_be.user.domain.dto.signUp.SignUpRequestDto;
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

    @PostMapping("/v1/api/member/signUp")
    public ApiResponse<UserPrivacy> signUp(@Valid @RequestBody SignUpRequestDto requestDto){

        UserPrivacy userPrivacy = userService.save(requestDto);

        return ApiResponse.success(HttpStatus.OK, userPrivacy);
    }

    @PostMapping("/v1/api/member/signIn")
    public Jwt signIn(@Valid @RequestBody LoginRequestDto requestDto) {

        log.info("request phoneNumber = {}, password = {}",
                requestDto.phoneNumber(), requestDto.password());

        Jwt jwt = userService.signIn(requestDto);

        log.info("jwt accessToken = {}, refreshToken = {}",
                jwt.getAccessToken(), jwt.getRefreshToken());

        return jwt;
    }

    @PostMapping("/test")
    public String test() {
        return "success";
    }
}
