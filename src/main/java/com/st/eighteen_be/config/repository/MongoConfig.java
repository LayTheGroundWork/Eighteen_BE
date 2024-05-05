package com.st.eighteen_be.config.repository;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * packageName    : com.st.eighteen_be.config.repository
 * fileName       : MongoConfig
 * author         : ipeac
 * date           : 24. 4. 16.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 4. 16.        ipeac       최초 생성
 */
@Configuration
@EnableMongoRepositories(basePackages = "com.st.eighteen_be.chat.repository.mongo")
@EnableMongoAuditing
public class MongoConfig {
}