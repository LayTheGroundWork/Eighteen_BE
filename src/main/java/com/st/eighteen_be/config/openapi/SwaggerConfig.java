package com.st.eighteen_be.config.openapi;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

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
@Profile(value = "!prod")
@Configuration
@RequiredArgsConstructor
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title("18_BE API")
                                .version("v1")
                                .description("18_BE API 명세서_v1")
                );
    }
    
    @Bean
    public GroupedOpenApi apiV1() {
        String[] paths = {"/v1/api**"};
        String[] packages = {"com.st.eighteen_be"};
        
        return GroupedOpenApi.builder()
                .packagesToScan(packages)
                .pathsToMatch(paths)
                .group("v1")
                .build();
    }
}