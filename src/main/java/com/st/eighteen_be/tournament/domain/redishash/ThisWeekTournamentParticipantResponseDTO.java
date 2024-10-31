package com.st.eighteen_be.tournament.domain.redishash;

import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO for {@link MostLikedUserRedisHash}
 */
@Builder
@Schema(description = "이번주 토너먼트 참가자 응답 DTO")
public record ThisWeekTournamentParticipantResponseDTO(
        @NotNull(message = "thisWeekTournamentNo must not be null")
        @Schema(description = "이번주 토너먼트 번호", example = "1")
        Long thisWeekTournamentNo,
        
        @NotNull(message = "userId must not be null")
        @Schema(description = "유저 아이디", example = "tester29")
        String userId,
        @NotNull(message = "category must not be null")
        @Schema(description = "카테고리", example = "ETC")
        String profileImageUrl,
        
        @NotNull(message = "userName must not be null")
        @Schema(description = "유저 이름", example = "장원영")
        String userName,
        
        @NotNull(message = "userSchool must not be null")
        @Schema(description = "유저 학교", example = "한국대학교")
        String userSchool,
        
        @NotNull(message = "userBirth must not be null")
        @Schema(description = "유저 생년월일", example = "1999-01-01")
        LocalDate userBirth
) implements Serializable {
        
        @QueryProjection
        public ThisWeekTournamentParticipantResponseDTO {
        }
}
