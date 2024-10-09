package com.st.eighteen_be.tournament.domain.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import com.st.eighteen_be.user.enums.CategoryType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class TournamentSearchResponseDTO {
    @Schema(description = "토너먼트 번호", example = "1")
    private Long tournamentNo;

    @Schema(description = "토너먼트 이미지 URL", example = "https://www.google.com")
    private String tournamentThumbnailUrl;

    @Schema(description = "토너먼트 상태", example = "true")
    private boolean status;

    @Schema(description = "토너먼트 종류", example = "ART")
    private String category;

    @Schema(description = "토너먼트 시작일", example = "2024-05-18T00:00:00")
    private LocalDateTime startDate;

    @Schema(description = "토너먼트 종료일", example = "2024-05-21T00:00:00")
    private LocalDateTime endDate;

    @Builder
    @QueryProjection
    public TournamentSearchResponseDTO(Long tournamentNo, String thumbnailUrl, boolean status, CategoryType category, LocalDateTime startDate, LocalDateTime endDate) {
        this.tournamentNo = tournamentNo;
        this.tournamentThumbnailUrl = thumbnailUrl;
        this.status = status;
        this.category = category.getDescription();
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
