package com.st.eighteen_be.message.api;

import com.st.eighteen_be.common.response.ApiResponse;
import com.st.eighteen_be.message.domain.dto.SmsCertificationRequestDto;
import com.st.eighteen_be.message.service.SmsUtil;
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
@RequestMapping("/v1/api/message")
public class MessageApiController {

    private final SmsUtil smsUtil;

    @PostMapping("/sends")
    public ApiResponse<String> sendSms(@Valid @RequestBody SmsCertificationRequestDto requestDto){

        smsUtil.sendOne(requestDto.getPhoneNumber());

        return ApiResponse.success(HttpStatus.OK,requestDto.getPhoneNumber() + ": 전송 완료");
    }

    @PostMapping("/confirms")
    public ApiResponse<String> smsVerification(@Valid @RequestBody SmsCertificationRequestDto requestDto) {

        smsUtil.verifySms(requestDto);

        return ApiResponse.success(HttpStatus.OK,"인증 완료");
    }
}
