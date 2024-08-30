package com.st.eighteen_be.message.api;

import com.st.eighteen_be.common.response.ApiResp;
import com.st.eighteen_be.message.dto.SmsCertificationRequestDto;
import com.st.eighteen_be.message.service.SmsUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "SMS API", description = "SMS API")
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping
public class MessageApiController {
    
    private final SmsUtil smsUtil;
    
    @Operation(summary = "SMS 전송", description = "SMS 인증번호를 전송합니다.")
    @PostMapping("/v1/api/message/send")
    public ApiResp<String> sendSms(@Valid @RequestBody SmsCertificationRequestDto requestDto) {
        
        smsUtil.sendOne(requestDto.getPhoneNumber());
        
        return ApiResp.success(HttpStatus.OK, requestDto.getPhoneNumber() + ": 전송 완료");
    }
    
    @Operation(summary = "SMS 인증", description = "SMS 인증번호를 확인합니다.")
    @PostMapping("/v1/api/message/confirms")
    public ApiResp<String> smsVerification(@Valid @RequestBody SmsCertificationRequestDto requestDto) {
        
        smsUtil.verifySms(requestDto);
        
        return ApiResp.success(HttpStatus.OK, "인증 완료");
    }
}