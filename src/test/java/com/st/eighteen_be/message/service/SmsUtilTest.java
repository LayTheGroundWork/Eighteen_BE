package com.st.eighteen_be.message.service;

import com.st.eighteen_be.common.annotation.ServiceWithRedisTest;
import com.st.eighteen_be.message.repository.SmsCertification;
import com.st.eighteen_be.user.dto.request.SignInRequestDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Collections;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ServiceWithRedisTest
@ExtendWith(MockitoExtension.class)
class SmsUtilTest {

    @MockBean
    private StringRedisTemplate stringRedisTemplate;

    private SmsUtil smsUtil;

    private SmsCertification smsCertification;

    private SignInRequestDto requestDto;

    private final String phone = "01012345678";
    private final String certificationNumber = "123456";

    @BeforeEach
    void setUp() {
        // Given
        smsCertification = new SmsCertification(stringRedisTemplate);
        smsUtil = new SmsUtil(smsCertification);

        requestDto = new SignInRequestDto(phone, certificationNumber);

        // 테스트 데이터 초기화
        ValueOperations<String, String> valueOperations = mock(ValueOperations.class);
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(stringRedisTemplate.delete("sms:" + phone)).thenReturn(true);
    }

    @Test
    public void verify() {
        //given
        ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
        when(valueOperations.get("sms:" + phone)).thenReturn(certificationNumber);
        when(smsUtil.isVerify(requestDto)).thenReturn(true);

        //when
        smsCertification.createSmsCertification(phone, certificationNumber);
        String storedCode = valueOperations.get("sms:" + phone);

        //then
        smsUtil.verifySms(requestDto);
        assertThat(storedCode).isEqualTo(certificationNumber);
    }

    @AfterEach
    void tearDown() {
        when(stringRedisTemplate.keys("spring:session:TEST:sessions:*")).thenReturn(Collections.singleton("spring:session:TEST:sessions:1"));
        for (String key : Objects.requireNonNull(stringRedisTemplate.keys("spring:session:TEST:sessions:*"))) {
            stringRedisTemplate.delete(key);
        }
    }
}
