package com.st.eighteen_be.common.basetime;

import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

/**
 * packageName    : com.st.eighteen_be.common.basetime
 * fileName       : BaseDocument
 * author         : ipeac
 * date           : 24. 4. 7.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 4. 7.        ipeac       최초 생성
 */
@Getter
public class BaseDocument {
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
}