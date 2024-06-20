package com.st.eighteen_be.config.openapi;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement; // SecurityRequirement import
import io.swagger.v3.oas.models.security.SecurityScheme; // SecurityScheme import
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
                )
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth")) // SecurityRequirement를 추가하여 인증 요구사항을 설정
                .components(new io.swagger.v3.oas.models.Components() // SecurityScheme을 추가하여 인증 스키마를 설정
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP) // HTTP 타입의 인증 스키마
                                        .scheme("bearer") // Bearer 토큰 사용
                                        .bearerFormat("JWT") // JWT 형식의 토큰
                                        .in(SecurityScheme.In.HEADER) // 헤더에 포함
                                        .name("Authorization") // 헤더 이름
                        )
                        .addSecuritySchemes("refreshToken",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.APIKEY) // API Key 타입의 인증 스키마
                                        .in(SecurityScheme.In.HEADER) // 헤더에 포함
                                        .name("Refresh") // 헤더 이름
                        )
                );
    }

    @Bean
    public GroupedOpenApi apiV1() {
        String[] paths = {"/v1/api/**"};
        String[] packages = {"com.st.eighteen_be"};

        return GroupedOpenApi.builder()
                .packagesToScan(packages)
                .pathsToMatch(paths)
                .group("v1")
                .build();
    }
}
