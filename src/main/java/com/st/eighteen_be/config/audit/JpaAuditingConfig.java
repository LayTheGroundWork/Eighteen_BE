package com.st.eighteen_be.config.audit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * packageName    : com.st.eighteen_be.config.audit
 * fileName       : AuditingConfig
 * author         : ipeac
 * date           : 2024-03-29
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-03-29        ipeac       최초 생성
 */
@Configuration
@EnableJpaAuditing
@Slf4j
public class JpaAuditingConfig {

}