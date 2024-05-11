package com.st.eighteen_be.chat.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "채팅방 조회 요청 DTO")
public record FindChatRoomRequestDTO(
        @Schema(description = "발신자 번호", example = "1")
        @Positive(message = "senderNo is not positive.")
        long senderNo
) {
    @Builder
    public FindChatRoomRequestDTO {
    }
}