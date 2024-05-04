package com.st.eighteen_be.message.service;

import com.st.eighteen_be.common.annotation.ServiceWithRedisTest;
import com.st.eighteen_be.message.domain.dto.SmsCertificationRequestDto;
import com.st.eighteen_be.message.repository.SmsCertification;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Objects;

@ServiceWithRedisTest
@ExtendWith(MockitoExtension.class)
class SmsUtilTest {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private SmsUtil smsUtil;

    @Autowired
    private SmsCertification smsCertification;

    private SmsCertificationRequestDto requestDto;

    @BeforeEach
    void setUp() {
        // Given
        smsUtil = new SmsUtil(smsCertification);

        requestDto = SmsCertificationRequestDto.builder()
                .phoneNumber("01012341234")
                .certificationNumber("1111")
                .build();
    }


    @Test
    public void verify() throws Exception {
        //given
        String phone = "01012341234";
        String code = "1111";

        //when
        smsCertification.createSmsCertification(phone,code);

        //then
        smsUtil.verifySms(new SmsCertificationRequestDto(phone,code));
    }

    @AfterEach
    void tearDown() {
        for (String key : Objects.requireNonNull(stringRedisTemplate.keys("spring:session:TEST:sessions:*"))) {
            stringRedisTemplate.delete(key);
        }
    }

}