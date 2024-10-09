package com.st.eighteen_be.tournament.api;

import com.st.eighteen_be.common.response.ApiResp;
import com.st.eighteen_be.tournament.domain.dto.request.TournamentVoteRequestDTO;
import com.st.eighteen_be.tournament.domain.dto.response.TournamentSearchResponseDTO;
import com.st.eighteen_be.tournament.domain.dto.response.TournamentVoteResultResponseDTO;
import com.st.eighteen_be.tournament.scheduler.TournamentScheduler;
import com.st.eighteen_be.tournament.service.TournamentService;
import com.st.eighteen_be.user.enums.CategoryType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * packageName    : com.st.eighteen_be.tournament.api
 * fileName       : TournamentApiController
 * author         : ipeac
 * date           : 24. 5. 15.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 5. 15.        ipeac       최초 생성
 */
@Tag(name = "토너먼트 API", description = "토너먼트 API")
@RestController
@RequiredArgsConstructor
public class TournamentApiController {
    private final TournamentService tournamentService;
    private final TournamentScheduler tournamentScheduler;

    @Operation(summary = "토너먼트 검색", description = "토너먼트를 조건에 맞게 검색하고, 페이징 처리하여 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(array = @ArraySchema(schema = @Schema(implementation = TournamentSearchResponseDTO.class)), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = ApiResp.class), mediaType = MediaType.APPLICATION_JSON_VALUE))
    })
    @GetMapping("/v1/api/tournament/search")
    public ApiResp<List<TournamentSearchResponseDTO>> search(
            @Parameter(description = "페이지 번호", example = "1", required = true)
            @RequestParam(value = "page", defaultValue = "1") int page,

            @Parameter(description = "페이지 크기", example = "10", required = true)
            @RequestParam(value = "size", defaultValue = "10") int size,

            @Parameter(description = "정렬 기준", example = "TOURNAMENT_NO")
            @RequestParam(value = "sort", defaultValue = "TOURNAMENT_NO") String sort,

            @Parameter(description = "정렬 방향", example = "DESC")
            @RequestParam(value = "sortDirection", defaultValue = "DESC") Sort.Direction sortDirection,

            @Parameter(description = "카테고리", example = "GAME")
            @RequestParam(value = "category", required = false) String category
    ) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(sortDirection, sort));

        List<TournamentSearchResponseDTO> responseDTOs = tournamentService.search(pageRequest, CategoryType.of(category));

        return ApiResp.success(HttpStatus.OK, responseDTOs);
    }

    @Operation(summary = "토너먼트 투표",
            description = "토너먼트에 투표합니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TournamentVoteRequestDTO.class))
            ),
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = ApiResp.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = ApiResp.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = ApiResp.class), mediaType = MediaType.APPLICATION_JSON_VALUE))
    })
    @PostMapping("/v1/api/tournament/final/vote")
    public ApiResp<Object> vote(@RequestBody TournamentVoteRequestDTO voteRequest, @RequestHeader("Authorization") String accessToken) {
        tournamentService.processVote(voteRequest, accessToken);

        return ApiResp.success(HttpStatus.OK, "토너먼트 투표 완료");
    }

    @Operation(summary = "토너먼트 최종 결과 조회",
            description = "토너먼트의 최종 결과를 조회합니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TournamentVoteRequestDTO.class))
            ))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(array = @ArraySchema(schema = @Schema(implementation = TournamentVoteResultResponseDTO.class)), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = ApiResp.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
    })
    @GetMapping("/v1/api/tournament/final/result")
    public ApiResp<List<TournamentVoteResultResponseDTO>> showResult(
            @Parameter(description = "토너먼트 번호", example = "1", required = true)
            @RequestParam(value = "tournamentNo") Long tournamentNo
    ) {
        return ApiResp.success(HttpStatus.OK, tournamentService.determineWinner(tournamentNo));
    }

    @Operation(summary = "토너먼트 강제 시작", description = "토너먼트를 강제로 시작합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = ApiResp.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
    })
    @PostMapping("/v1/api/tournament/force-start")
    public ApiResp<Object> startTournament() {
        tournamentService.startTournament();

        return ApiResp.success(HttpStatus.OK, "토너먼트 시작 완료");
    }

    @Operation(summary = "토너먼트 강제 종료", description = "토너먼트를 강제로 종료합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = ApiResp.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = ApiResp.class), mediaType = MediaType.APPLICATION_JSON_VALUE))
    })
    @PostMapping("/v1/api/tournament/force-end")
    public ApiResp<Object> endTournament() {
        tournamentService.endLastestTournaments();

        return ApiResp.success(HttpStatus.OK, "토너먼트 종료 완료");
    }

    @Operation(summary = "메모리에 강제로 랜덤 유저를 올립니다.", description = "메모리에 강제로 랜덤 유저를 올립니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = ApiResp.class), mediaType
                    = MediaType.APPLICATION_JSON_VALUE)),
    })
    @PutMapping("/v1/api/tournament/force-pick-random-user-to-redis")
    public ApiResp<Object> pickRandomUser() {
        tournamentScheduler.pickRandomUser();

        return ApiResp.success(HttpStatus.OK, "랜덤 유저 선정 완료");
    }
}
