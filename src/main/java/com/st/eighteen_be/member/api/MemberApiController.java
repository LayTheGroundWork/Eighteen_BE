package com.st.eighteen_be.member.api;

import com.st.eighteen_be.common.response.ApiResponse;
import com.st.eighteen_be.member.domain.MemberPrivacy;
import com.st.eighteen_be.member.domain.dto.signIn.SignInRequestDto;
import com.st.eighteen_be.member.service.MemberService;
import com.st.eighteen_be.message.service.SmsUtil;
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

    private final MemberService memberService;
    private final SmsUtil smsUtil;

    @PostMapping("/signIn")
    public ApiResponse<MemberPrivacy> signIn(@Valid @RequestBody SignInRequestDto requestDto){

        return ApiResponse.success(HttpStatus.OK, memberService.save(requestDto));
    }

    @PostMapping("/sms-certification/sends")
    public ApiResponse<String> sendsCertification(String phoneNumber){

        smsUtil.sendOne(phoneNumber, smsUtil.createRandomNumber());

        return ApiResponse.success(HttpStatus.OK, phoneNumber + ": 전송 완료");
    }
}
