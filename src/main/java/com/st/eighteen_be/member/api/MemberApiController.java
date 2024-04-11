package com.st.eighteen_be.member.api;

import com.st.eighteen_be.common.response.ApiResponse;
import com.st.eighteen_be.member.domain.dto.signIn.SignInRequestDto;
import com.st.eighteen_be.member.domain.dto.signIn.SignInResponseDto;
import com.st.eighteen_be.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * packageName    : com.st.eighteen_be.member.web
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

    private final MemberService memberService;

    @PostMapping("/save")
    public ApiResponse<SignInResponseDto> save(@Valid @RequestBody SignInRequestDto requestDto){

        memberService.signIn(requestDto);
        return ApiResponse.success(HttpStatus.OK, );
    }

}
