package com.st.eighteen_be.tournament.domain.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

/**
 * packageName    : com.st.eighteen_be.tournament.api
 * fileName       : TournamentSearchResponseDTO
 * author         : ipeac
 * date           : 24. 5. 18.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 5. 18.        ipeac       최초 생성
 */
@Getter
@Setter
@Builder
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor
public class TournamentSearchResponseDTO {
    @Schema(description = "토너먼트 번호", example = "1")
    private Long tournamentNo;
    
    @Schema(description = "토너먼트 이미지 URL", example = "https://www.google.com")
    private String tournamentThumbnailUrl;
    
    @Schema(description = "토너먼트 상태", example = "true")
    private boolean status;
    
    @Schema(description = "토너먼트 시작일", example = "2024-05-18T00:00:00")
    private LocalDateTime startDate;
    
    @Schema(description = "토너먼트 종료일", example = "2024-05-21T00:00:00")
    private LocalDateTime endDate;
}