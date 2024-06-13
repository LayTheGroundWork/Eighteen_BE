package com.st.eighteen_be.user.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Service
public class SchoolService {

    private final WebClient webClient;

    @Value("${neis.key}")
    private String apiKey;

    public SchoolService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://open.neis.go.kr").build();
    }

    public Mono<String> searchSchools(String schoolName) {
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
                .onErrorResume(WebClientResponseException.class, ex -> {
                    // 에러 처리 로직 추가
                    return Mono.error(new RuntimeException("API 호출 실패: " + ex.getMessage()));
                });
    }



}
