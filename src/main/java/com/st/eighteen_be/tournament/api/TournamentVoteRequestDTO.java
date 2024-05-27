package com.st.eighteen_be.tournament.api;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

/**
 * packageName    : com.st.eighteen_be.tournament.api
 * fileName       : TournamentVoteRequestDTO
 * author         : ipeac
 * date           : 24. 5. 27.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 5. 27.        ipeac       최초 생성
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class TournamentVoteRequestDTO {
    @NotNull
    @Schema(description = "토너먼트 참가자 ID", example = "user1")
    private String participantId;
    
    @PositiveOrZero
    @Schema(description = "투표 점수", example = "1")
    private int votePoint;
}