package com.st.eighteen_be.chat.model.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * packageName    : com.st.eighteen_be.chat.model.dto.request
 * fileName       : EnterChatRoomRequestDTO
 * author         : ipeac
 * date           : 24. 4. 12.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 4. 12.        ipeac       최초 생성
 */
public record EnterChatRoomRequestDTO(
        @NotNull(message = "postNo is required")
        Long postNo,
        @NotNull(message = "memberNo is required")
        Long memberNo,
        @PastOrPresent(message = "requestTime should be past or present")
        LocalDateTime requestTime
) {
    @Builder
    public EnterChatRoomRequestDTO {
    }
}