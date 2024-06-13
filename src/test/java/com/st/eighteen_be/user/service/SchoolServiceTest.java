package com.st.eighteen_be.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@DisplayName("SchoolService 테스트")
@ActiveProfiles("Test")
@SpringBootTest
class SchoolServiceTest {

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @Autowired
    private SchoolService schoolService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(webClientBuilder.baseUrl(anyString())).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    }

    @Test
    public void school_search() throws Exception {
        //given
        String mockResponse = "{\"schoolList\": [{\"name\": \"서울고등학교\"}]}";
        String keyword = "서울";

        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just(mockResponse));

        //when
        Mono<String> find_school = schoolService.searchSchools(keyword);

        //then
        String result = find_school.block(); // Mono를 블록하여 결과를 가져옴
        assertThat(result).isEqualTo(mockResponse);
    }

    @Test
    public void school_search_failure() throws Exception {
        //given
        String keyword = "서울";

        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.error(new WebClientResponseException(500, "Internal Server Error", null, null, null)));

        //when
        Mono<String> find_school = schoolService.searchSchools(keyword);

        //then
        RuntimeException exception = assertThrows(RuntimeException.class, find_school::block);
        assertThat(exception.getMessage()).contains("API 호출 실패");
    }
}
