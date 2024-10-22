package com.st.eighteen_be.tournament.domain.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import com.st.eighteen_be.user.enums.CategoryType;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

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
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@Schema(description = "토너먼트 정보 응답 DTO")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class TournamentSearchResponseDTO {
    @Schema(description = "토너먼트 종류", example = "ART")
    private String category;
    
    @ArraySchema(schema = @Schema(description = "토너먼트 우승자 응답 DTO", example = "[{\"tournamentNo\":1,\"round\":1,\"profileImageUrl\":\"https://picsum.photos/200\"}]"))
    private List<TournamentWinnerResponseDTO> winner;
    
    @QueryProjection
    public TournamentSearchResponseDTO(CategoryType category, List<TournamentWinnerResponseDTO> winner) {
        this.category = category.getCategory();
        this.winner = winner;
    }
    
    @Schema(description = "토너먼트 우승자 응답 DTO")
    @Getter
    public static class TournamentWinnerResponseDTO {
        @Schema(description = "토너먼트 번호", example = "1")
        private Long tournamentNo;
        
        @Schema(description = "토너먼트 회차", example = "1")
        private Integer round;
        
        @Schema(description = "우승자 사진 url", example = "https://picsum.photos/200")
        private String profileImageUrl;
        
        @QueryProjection
        public TournamentWinnerResponseDTO(Long tournamentNo, Integer round, String profileImageUrl) {
            this.tournamentNo = tournamentNo;
            this.round = round;
            this.profileImageUrl = profileImageUrl;
        }
    }
}
