package com.st.eighteen_be.school.service;

import com.st.eighteen_be.school.dto.SchoolsResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
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
        String schoolName = "서울고등학교";

//        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just(mockResponse));

        //when
        Mono<List<SchoolsResponseDto>> find_school = schoolService.searchSchools(schoolName);

        //then
        SchoolsResponseDto result = Objects.requireNonNull(find_school.block()).get(0); // Mono를 블록하여 결과를 가져옴
        assertThat(result.getSchoolName()).isEqualTo(schoolName);
    }

}
