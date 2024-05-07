package com.st.eighteen_be.common.annotation;

import com.st.eighteen_be.config.redis.RedisConfig;
import com.st.eighteen_be.message.repository.SmsCertification;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// 테스트용 어노테이션 생성
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@DataRedisTest
@ActiveProfiles("test")
@Import({RedisConfig.class, SmsCertification.class}) // Redis 설정 클래스를 여기에 추가하세요.
public @interface ServiceWithRedisTest {
}