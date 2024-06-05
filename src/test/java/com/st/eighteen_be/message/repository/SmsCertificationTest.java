package com.st.eighteen_be.message.repository;

import com.st.eighteen_be.common.annotation.ServiceWithRedisTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@ServiceWithRedisTest
class SmsCertificationTest {

    private SmsCertification smsCertification;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private final String phone = "01033781934";
    private final String certificationNumber = "1111";

    @BeforeEach
    void setUp() {
        smsCertification = new SmsCertification(stringRedisTemplate);
        // 테스트 데이터 초기화
        stringRedisTemplate.delete("sms:" + phone);
    }

    @Test
    void testCreateSmsCertification() {
        smsCertification.createSmsCertification(phone, certificationNumber);
        String storedCertificationNumber = stringRedisTemplate.opsForValue().get("sms:" + phone);

        assertThat(storedCertificationNumber).isEqualTo(certificationNumber);
    }

    @Test
    void testGetSmsCertification() {
        stringRedisTemplate.opsForValue().set("sms:" + phone, certificationNumber);
        String fetchedCertificationNumber = smsCertification.getSmsCertification(phone);

        assertThat(fetchedCertificationNumber).isEqualTo(certificationNumber);
    }

    @Test
    void testDeleteSmsCertification() {
        stringRedisTemplate.opsForValue().set("sms:" + phone, certificationNumber);
        smsCertification.deleteSmsCertification(phone);

        assertThat(stringRedisTemplate.hasKey("sms:" + phone)).isFalse();
    }

    @Test
    void testHasKey() {
        stringRedisTemplate.opsForValue().set("sms:" + phone, certificationNumber);
        boolean exists = smsCertification.hasKey(phone);

        assertThat(exists).isTrue();
    }
}
