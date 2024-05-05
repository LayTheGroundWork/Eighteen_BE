package com.st.eighteen_be.common.annotation;

import com.st.eighteen_be.common.extension.MongodbTestContainerExtenstion;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// 테스트용 어노테이션 생성
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ActiveProfiles("test")
@DataMongoTest
@ExtendWith(MongodbTestContainerExtenstion.class)
public @interface ServiceWithMongoDBTest {
}