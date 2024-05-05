package com.st.eighteen_be.chat.model.dto.request;

import jakarta.validation.constraints.Positive;
import lombok.Builder;

/**
 * packageName    : com.st.eighteen_be.chat.model.dto.request
 * fileName       : FindChatRoomRequestDTO
 * author         : ipeac
 * date           : 24. 5. 6.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 5. 6.        ipeac       최초 생성
 */
public record FindChatRoomRequestDTO(
        @Positive(message = "senderNo is not positive.")
        long senderNo
) {
    @Builder
    public FindChatRoomRequestDTO {
    }
}