package com.st.eighteen_be.test.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * packageName    : com.st.eighteen_be.test.api
 * fileName       : TestApiController
 * author         : ipeac
 * date           : 24. 5. 4.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 5. 4.        ipeac       최초 생성
 */
@RestController
@RequestMapping("/v1/api/test")
@Tag(name = "Test", description = "테스트 API")
public class TestApiController {
    
    @GetMapping("/test")
    @Operation(summary = "테스트 API", description = "테스트 API 입니다.")
    public ResponseEntity<Object> test() {
        return ResponseEntity.ok("test");
    }
}