package com.st.eighteen_be.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * packageName    : com.st.eighteen_be.config
 * fileName       : TestController
 * author         : ipeac
 * date           : 2024-03-27
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-03-27        ipeac       최초 생성
 */
@SpringBootTest
@AutoConfigureMockMvc
public class TestControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    @DisplayName("에러 발생시 ErrorResponse를 반환한다.")
    public void When_ThrowException_Expect_ErrorResponse() throws Exception {
        mockMvc.perform(get("/test"))
                .andExpect(status().isNotFound());
    }
    
}