package com.st.eighteen_be.config.openapi;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * packageName    : com.st.eighteen_be.config.openapi
 * fileName       : OpenApiConfig
 * author         : ipeac
 * date           : 24. 5. 5.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 5. 5.        ipeac       최초 생성
 */
@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI apiV1() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title("18_BE API")
                                .version("v1")
                                .description("18_BE API 명세서_v1")
                );
    }
}