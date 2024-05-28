package com.st.eighteen_be.tournament.api;

import com.st.eighteen_be.common.exception.ErrorCode;
import com.st.eighteen_be.common.exception.sub_exceptions.data_exceptions.BadRequestException;
import com.st.eighteen_be.tournament.domain.entity.TournamentEntity;
import com.st.eighteen_be.tournament.domain.entity.TournamentParticipantEntity;
import com.st.eighteen_be.tournament.domain.entity.VoteEntity;
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
    @Schema(description = "투표 대상 토너먼트 ID", example = "1")
    private Long tournamentNo;
    
    @NotNull
    @Schema(description = "누가 투표했는지 ID", example = "voter1")
    private String voterId;
    
    @NotNull
    @Schema(description = "누구에게 투표했는지 ID", example = "user1")
    private String voteeId;
    
    @PositiveOrZero
    @Schema(description = "투표 점수", example = "1")
    private int votePoint;
    
    public VoteEntity toEntity(TournamentEntity tournamentEntity, TournamentParticipantEntity participantEntity) {
        if (tournamentEntity == null || participantEntity == null) {
            throw new BadRequestException(ErrorCode.INVALID_PARAMETER);
        }
        
        return VoteEntity.builder()
                .voterId(voterId)
                .tournament(tournamentEntity)
                .participant(participantEntity)
                .votePoint(votePoint)
                .build();
    }
}