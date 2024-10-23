package com.st.eighteen_be.tournament.domain.dto.request;

import com.st.eighteen_be.common.exception.ErrorCode;
import com.st.eighteen_be.common.exception.sub_exceptions.data_exceptions.BadRequestException;
import com.st.eighteen_be.tournament.domain.entity.TournamentEntity;
import com.st.eighteen_be.tournament.domain.entity.TournamentParticipantEntity;
import com.st.eighteen_be.tournament.domain.entity.VoteEntity;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

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
    @Schema(description = "투표 대상 토너먼트 ID", example = "1", requiredMode = REQUIRED)
    private Long tournamentNo;
    
    @ArraySchema(schema = @Schema(description = "참여자들의 아이디, 등수순으로", example = "[\"tester22\", \"tester21\", \"tester20\", \"tester19\"]"), uniqueItems = true)
    private List<String> participantIdsOrderByRank;

    public VoteEntity toEntity(TournamentEntity tournamentEntity, TournamentParticipantEntity participantEntity, int point, String loginedUserId) {
        if (tournamentEntity == null || participantEntity == null) {
            throw new BadRequestException(ErrorCode.INVALID_PARAMETER);
        }

        return VoteEntity.builder()
                .tournament(tournamentEntity)
                .participant(participantEntity)
                .voterId(loginedUserId)
                .votePoint(point)
                .build();
    }
}
