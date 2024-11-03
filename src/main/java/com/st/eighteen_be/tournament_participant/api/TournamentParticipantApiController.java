package com.st.eighteen_be.tournament_participant.api;

import com.st.eighteen_be.common.response.ApiResp;
import com.st.eighteen_be.common.security.SecurityUtil;
import com.st.eighteen_be.tournament.domain.redishash.ThisWeekTournamentParticipantResponseDTO;
import com.st.eighteen_be.tournament_participant.service.TournamentParticipantService;
import com.st.eighteen_be.user.enums.CategoryType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * packageName    : com.st.eighteen_be.tournament_participant.api
 * fileName       : TournamentParticipantApiController
 * author         : ipeac
 * date           : 24. 10. 23.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 10. 23.        ipeac       최초 생성
 */
@Tag(name = "토너먼트 참가자 API")
@RestController
@RequiredArgsConstructor
public class TournamentParticipantApiController {
    private final TournamentParticipantService tournamentParticipantService;
    
    @Operation(summary = "이번주 토너먼트 참가자 조회",
            description = "이번주 토너먼트 참가자 조회")
    @ApiResponse(responseCode = "200", description = "OK")
    @GetMapping("/v1/api/tournament/participant/{category}/this-week")
    public ApiResp<List<ThisWeekTournamentParticipantResponseDTO>> showParticipantNowTournament(
            @Parameter(description = "카테고리", required = true)
            @PathVariable("category") CategoryType category,
            
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        SecurityUtil.checkUser(userDetails);
        
        return ApiResp.success(HttpStatus.OK, tournamentParticipantService.showParticipantNowTournament(category, userDetails.getUsername()));
    }
}
