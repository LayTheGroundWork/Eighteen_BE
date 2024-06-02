package com.st.eighteen_be.message.service;

import com.st.eighteen_be.common.annotation.ServiceWithRedisTest;
import com.st.eighteen_be.message.repository.SmsCertification;
import com.st.eighteen_be.user.dto.sign.SignInRequestDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@ServiceWithRedisTest
@ExtendWith(MockitoExtension.class)
class SmsUtilTest {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private SmsUtil smsUtil;

    private SmsCertification smsCertification;

    private SignInRequestDto requestDto;

    private final String phone = "01012345678";
    private final String certificationNumber = "123456";

    @BeforeEach
    void setUp() {
        // Given
        smsUtil = new SmsUtil(smsCertification);

        requestDto = new SignInRequestDto(phone,certificationNumber);


        // 테스트 데이터 초기화
        stringRedisTemplate.delete("sms:" + phone);
    }


    @Test
    public void verify() {

        //when
        smsCertification.createSmsCertification(phone,certificationNumber);
        String storedCode = stringRedisTemplate.opsForValue().get("sms:" + phone);

        //then
        smsUtil.verifySms(requestDto);
        assertThat(storedCode).isEqualTo(certificationNumber);
    }

    @AfterEach
    void tearDown() {
        for (String key : Objects.requireNonNull(stringRedisTemplate.keys("spring:session:TEST:sessions:*"))) {
            stringRedisTemplate.delete(key);
        }
    }

}