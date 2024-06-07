package com.st.eighteen_be.tournament.domain.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * packageName    : com.st.eighteen_be.tournament.service
 * fileName       : TournamentVoteResultResponseDTO
 * author         : ipeac
 * date           : 24. 5. 21.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 5. 21.        ipeac       최초 생성
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class TournamentVoteResultResponseDTO {
    @Schema(description = "랭커 ID", example = "user1")
    private String rankerId;
    @Schema(description = "랭크", example = "1")
    private Long rank;
    @Schema(description = "투표 수", example = "10")
    private Long voteCount;
    @Schema(description = "프로필 이미지 URL", example = "https://picsum.photos/200")
    private String profileImageUrl;
    
    @Builder
    @QueryProjection
    public TournamentVoteResultResponseDTO(String rankerId, Long voteCount, String profileImageUrl) {
        this.rankerId = rankerId;
        this.voteCount = voteCount;
        this.profileImageUrl = profileImageUrl;
    }
    
    public static TournamentVoteResultResponseDTO of(String rankerId, Long voteCount) {
        return TournamentVoteResultResponseDTO.builder()
                       .rankerId(rankerId)
                       .voteCount(voteCount)
                       .build();
    }
}