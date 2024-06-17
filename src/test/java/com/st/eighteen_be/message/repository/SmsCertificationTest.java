package com.st.eighteen_be.message.repository;

import com.st.eighteen_be.common.annotation.ServiceWithRedisTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ServiceWithRedisTest
class SmsCertificationTest {

    private SmsCertification smsCertification;

    @MockBean
    private StringRedisTemplate stringRedisTemplate;

    private final String phone = "01033781934";
    private final String certificationNumber = "123456";

    @BeforeEach
    void setUp() {
        smsCertification = new SmsCertification(stringRedisTemplate);

        // ValueOperations 모킹
        ValueOperations<String, String> valueOperations = mock(ValueOperations.class);
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);

        // 테스트 데이터 초기화
        when(stringRedisTemplate.delete("sms:" + phone)).thenReturn(true);
    }

    @Test
    void testCreateSmsCertification() {
        ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
        doNothing().when(valueOperations).set("sms:" + phone, certificationNumber, Duration.ofMinutes(3));

        smsCertification.createSmsCertification(phone, certificationNumber);
        verify(valueOperations, times(1)).set("sms:" + phone, certificationNumber, Duration.ofMinutes(3));
    }

    @Test
    void testGetSmsCertification() {
        ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
        when(valueOperations.get("sms:" + phone)).thenReturn(certificationNumber);

        String fetchedCertificationNumber = smsCertification.getSmsCertification(phone);

        assertThat(fetchedCertificationNumber).isEqualTo(certificationNumber);
    }

    @Test
    void testDeleteSmsCertification() {
        when(stringRedisTemplate.delete("sms:" + phone)).thenReturn(true);

        smsCertification.deleteSmsCertification(phone);

        verify(stringRedisTemplate, times(1)).delete("sms:" + phone);
    }

    @Test
    void testHasKey() {
        when(stringRedisTemplate.hasKey("sms:" + phone)).thenReturn(true);

        boolean exists = smsCertification.hasKey(phone);

        assertThat(exists).isTrue();
    }
}
