package com.st.eighteen_be.test.api;

import org.springframework.http.ResponseEntity;
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
@RequestMapping("/api/test")
public class TestApiController {
    
    public ResponseEntity<Object> test() {
        return ResponseEntity.ok("test");
    }
}