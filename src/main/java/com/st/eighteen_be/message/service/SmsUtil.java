package com.st.eighteen_be.message.service;

import com.st.eighteen_be.common.exception.ErrorCode;
import com.st.eighteen_be.common.exception.sub_exceptions.data_exceptions.AuthenticationException;
import com.st.eighteen_be.message.repository.SmsCertification;
import com.st.eighteen_be.user.dto.request.SignInRequestDto;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class SmsUtil {
    private final SmsCertification smsCertification;

    @Value("${coolsms.apikey}")
    private String apiKey;

    @Value("${coolsms.apisecret}")
    private String apiSecret;

    @Value("${coolsms.fromnumber}")
    private String fromNumber;

    private DefaultMessageService messageService;

    @PostConstruct
    private void init(){
        this.messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecret, "https://api.coolsms.co.kr");
    }

    private String createRandomNumber() {
        int digits = 6;
        SecureRandom rand = new SecureRandom();
        StringBuilder randomNum = new StringBuilder();

        for (int i = 0; i < digits; i++) {
            String random = Integer.toString(rand.nextInt(10));
            randomNum.append(random);
        }

        return randomNum.toString();
    }

    // 단일 메시지 발송
    public void sendOne(String to) {
        String verificationCode = createRandomNumber();
        Message message = new Message();

        // 발신번호 및 수신번호는 반드시 01012345678 형태로 입력되어야 합니다.
        message.setFrom(fromNumber);
        message.setTo(to);
        message.setText("[A-Teen] 아래의 인증번호를 입력해주세요\n" + verificationCode);

        smsCertification.createSmsCertification(to,verificationCode);
        this.messageService.sendOne(new SingleMessageSendingRequest(message));
    }

    // 사용자가 입력한 인증번호가 Redis에 저장된 인증번호와 동일한지 확인
    public void verifySms(SignInRequestDto requestDto) {
        if (isVerify(requestDto)) {
            throw new AuthenticationException(ErrorCode.AUTHENTICATION_NUMBER_MISMATCH);
        }
        smsCertification.deleteSmsCertification(requestDto.phoneNumber());
    }

    public boolean isVerify(SignInRequestDto requestDto) {
        return !(smsCertification.hasKey(requestDto.phoneNumber()) &&
                 smsCertification.getSmsCertification(requestDto.phoneNumber())
                        .equals(requestDto.verificationCode()));
    }
}
