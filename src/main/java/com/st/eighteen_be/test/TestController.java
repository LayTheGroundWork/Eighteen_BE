package com.st.eighteen_be.test;

import com.st.eighteen_be.common.exception.ErrorCode;
import com.st.eighteen_be.common.exception.sub_exceptions.data_exceptions.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * packageName    : com.st.eighteen_be.test
 * fileName       : TestController
 * author         : ipeac
 * date           : 2024-03-27
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-03-27        ipeac       최초 생성
 */
@RestController
public class TestController {
    
    @GetMapping("/test")
    public ResponseEntity test() {
        
        throw new NotFoundException(ErrorCode.TEST_ERROR);
    }
}