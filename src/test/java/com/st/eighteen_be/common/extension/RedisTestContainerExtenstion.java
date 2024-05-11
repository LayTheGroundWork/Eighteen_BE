package com.st.eighteen_be.common.extension;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

/**
 * packageName    : com.st.eighteen_be.chat.service.redis
 * fileName       : RedisTestContainerExtenstion
 * author         : ipeac
 * date           : 24. 5. 11.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 5. 11.        ipeac       최초 생성
 */
public class RedisTestContainerExtenstion implements BeforeAllCallback {
    private static final DockerImageName REDIS_IMAGE = DockerImageName.parse("bitnami/redis:7.2.4");

    @Container
    private static final GenericContainer<?> REDIS_CONTAINER = new GenericContainer<>(REDIS_IMAGE)
            .withExposedPorts(6379)
            .withEnv("ALLOW_EMPTY_PASSWORD", "yes");

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        if (REDIS_CONTAINER.isRunning()) {
            return;
        }

        try {
            REDIS_CONTAINER.start();

            System.setProperty("spring.redis.host", REDIS_CONTAINER.getHost());
            System.setProperty("spring.redis.port", REDIS_CONTAINER.getMappedPort(6379).toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}