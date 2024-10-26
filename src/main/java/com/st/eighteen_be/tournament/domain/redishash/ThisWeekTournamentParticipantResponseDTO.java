package com.st.eighteen_be.tournament.domain.redishash;

import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.io.Serializable;

/**
 * DTO for {@link MostLikedUserRedisHash}
 */
@Builder
@Schema(description = "이번주 토너먼트 참가자 응답 DTO")
public record ThisWeekTournamentParticipantResponseDTO(
        @NotNull(message = "userId must not be null")
        @Schema(description = "유저 아이디", example = "tester29")
        String userId,
        @NotNull(message = "category must not be null")
        @Schema(description = "카테고리", example = "ETC")
        String profileImageUrl
) implements Serializable {
        
        @QueryProjection
        public ThisWeekTournamentParticipantResponseDTO {
        }
}
