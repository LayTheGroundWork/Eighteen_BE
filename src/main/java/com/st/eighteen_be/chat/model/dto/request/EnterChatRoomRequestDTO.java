package com.st.eighteen_be.chat.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "채팅방 입장 요청 DTO")
public record EnterChatRoomRequestDTO(
        @Schema(description = "발신자 번호", example = "1")
        @NotNull(message = "senderNo is required")
        Long senderNo,
        
        @Schema(description = "수신자 번호", example = "2")
        @NotNull(message = "receiverNo is required")
        Long receiverNo,
        
        @Schema(description = "요청 시간", example = "2021-04-12T00:00:00")
        @PastOrPresent(message = "requestTime should be past or present")
        LocalDateTime requestTime
) {
    @Builder
    public EnterChatRoomRequestDTO {
    }
}