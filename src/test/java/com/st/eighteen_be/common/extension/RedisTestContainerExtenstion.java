package com.st.eighteen_be.common.extension;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
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
public class RedisTestContainerExtenstion implements AfterAllCallback {
    private static final DockerImageName REDIS_IMAGE = DockerImageName.parse("bitnami/redis:7.2.4");
    private static final GenericContainer<?> REDIS_CONTAINER = new GenericContainer<>(REDIS_IMAGE)
            .withExposedPorts(6379)
            .withReuse(true)
            .withEnv("ALLOW_EMPTY_PASSWORD", "yes");

    static {
        REDIS_CONTAINER.start();
    }

    @DynamicPropertySource
    public static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);
        registry.add("spring.data.redis.port", () -> "" + REDIS_CONTAINER.getMappedPort(6379));
    }

    @Override
    public void afterAll(ExtensionContext context) {
        if (REDIS_CONTAINER.isRunning()) {
            REDIS_CONTAINER.stop();
        }
    }
}
