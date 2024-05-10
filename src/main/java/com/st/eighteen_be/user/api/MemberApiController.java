package com.st.eighteen_be.user.api;

import com.st.eighteen_be.common.response.ApiResponse;
import com.st.eighteen_be.user.domain.dto.LoginRequestDto;
import com.st.eighteen_be.user.domain.dto.signIn.SignInRequestDto;
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
@RequestMapping("/v1/api/member")
public class MemberApiController {

    private final UserService userService;

    @PostMapping("/signIn")
    public ApiResponse<SignInRequestDto> signIn(@Valid @RequestBody SignInRequestDto requestDto){

        userService.save(requestDto);
        return ApiResponse.success(HttpStatus.OK, requestDto);
    }

    @PostMapping("/login")
    public ApiResponse<LoginRequestDto> login(@Valid @RequestBody LoginRequestDto requestDto) {

        userService.login(requestDto);
        return ApiResponse.success(HttpStatus.OK, requestDto);
    }
}
