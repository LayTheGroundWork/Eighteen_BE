package com.st.eighteen_be;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = "de.flapdoodle.mongodb.embedded.version=7.0.8")
class EighteenBeApplicationTests {
    
    @Test
    void contextLoads() {
    }
}