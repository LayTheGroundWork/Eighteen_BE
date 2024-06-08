package com.st.eighteen_be.message.api;

import com.st.eighteen_be.common.response.ApiResp;
import com.st.eighteen_be.message.dto.SmsCertificationRequestDto;
import com.st.eighteen_be.message.service.SmsUtil;
import com.st.eighteen_be.user.dto.request.SignInRequestDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping
public class MessageApiController {

    private final SmsUtil smsUtil;

    @PostMapping("/v1/api/message/send")
    public ApiResp<String> sendSms(@Valid @RequestBody SmsCertificationRequestDto requestDto){

        smsUtil.sendOne(requestDto.getPhoneNumber());

        return ApiResp.success(HttpStatus.OK,requestDto.getPhoneNumber() + ": 전송 완료");
    }

    @PostMapping("/v1/api/message/confirms")
    public ApiResp<String> smsVerification(@Valid @RequestBody SignInRequestDto requestDto) {

        smsUtil.verifySms(requestDto);

        return ApiResp.success(HttpStatus.OK,"인증 완료");
    }
}
