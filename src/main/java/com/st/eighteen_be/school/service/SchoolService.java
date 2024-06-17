package com.st.eighteen_be.school.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.st.eighteen_be.school.dto.SchoolsResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
public class SchoolService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${neis.key}")
    private String apiKey;

    public SchoolService(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.webClient = webClientBuilder.baseUrl("https://open.neis.go.kr").build();
        this.objectMapper = objectMapper;
    }

    public Mono<List<SchoolsResponseDto>> searchSchools(String schoolName) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/hub/schoolInfo")
                        .queryParam("KEY", apiKey)
                        .queryParam("Type", "json")
                        .queryParam("pIndex", "1")
                        .queryParam("pSize", "100")
                        .queryParam("SCHUL_NM", schoolName)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(response -> {
                    try {
                        JsonNode root = objectMapper.readTree(response);
                        JsonNode schoolList = root.path("schoolInfo").path(1).path("row");
                        List<SchoolsResponseDto> schools = new ArrayList<>();
                        if (schoolList.isArray()) {
                            for (JsonNode schoolNode : schoolList) {
                                SchoolsResponseDto schoolInfo =
                                        new SchoolsResponseDto(schoolNode.path("SCHUL_NM").asText(),
                                                schoolNode.path("ORG_RDNMA").asText());
                                schools.add(schoolInfo);
                            }
                        }
                        return Mono.just(schools);
                    } catch (Exception e) {
                        return Mono.error(new RuntimeException("JSON 파싱 실패: " + e.getMessage()));
                    }
                })
                .onErrorResume(WebClientResponseException.class, ex -> {
                    // 에러 처리 로직 추가
                    return Mono.error(new RuntimeException("API 호출 실패: " + ex.getMessage()));
                });
    }
}
