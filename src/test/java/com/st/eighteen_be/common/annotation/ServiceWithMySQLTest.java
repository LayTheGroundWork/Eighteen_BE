package com.st.eighteen_be.common.annotation;

import com.st.eighteen_be.config.advice.audit.JpaAuditingConfig;
import com.st.eighteen_be.config.querydsl.QuerydslConfig;
import com.st.eighteen_be.config.repository.JpaConfig;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// 테스트용 어노테이션 생성
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY ,connection = EmbeddedDatabaseConnection.H2)
@Import({JpaAuditingConfig.class, JpaConfig.class, QuerydslConfig.class})
@DataJpaTest
@ActiveProfiles("test")
public @interface ServiceWithMySQLTest {
}
