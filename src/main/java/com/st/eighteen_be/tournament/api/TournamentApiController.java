package com.st.eighteen_be.tournament.api;

import com.st.eighteen_be.common.response.ApiResp;
import com.st.eighteen_be.tournament.domain.dto.response.TournamentSearchResponseDTO;
import com.st.eighteen_be.tournament.domain.enums.TournamentCategoryEnums;
import com.st.eighteen_be.tournament.service.TournamentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
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
@Tag(name = "TournamentApiController", description = "토너먼트 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/")
public class TournamentApiController {
    private final TournamentService tournamentService;
    
    @Operation(summary = "토너먼트 검색", description = "토너먼트를 조건에 맞게 검색하고, 페이징 처리하여 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "Not Found"),
    })
    @GetMapping("/v1/tournament/search")
    public ApiResp<List<TournamentSearchResponseDTO>> search(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "TOURNAMENT_NO") String sort,
            @RequestParam(value = "sortDirection", defaultValue = "DESC") Sort.Direction sortDirection,
            @RequestParam(value = "category", required = false) String category
    ) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        
        List<TournamentSearchResponseDTO> responseDTOs = tournamentService.search(pageRequest, TournamentCategoryEnums.findByCategoryOrNull(category));
        
        return ApiResp.success(HttpStatus.OK, responseDTOs);
    }
    
    @Operation(summary = "토너먼트 투표", description = "토너먼트에 투표합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
    })
    @PostMapping("/v1/tournament/final/vote")
    public ApiResp<Object> vote() {
        return ApiResp.success(HttpStatus.OK, "test");
    }
    
    @Operation(summary = "토너먼트 강제 시작", description = "토너먼트를 강제로 시작합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
    })
    @PostMapping("/v1/tournament/force-start")
    public ApiResp<Object> startTournament() {
        tournamentService.startTournament();
        
        return ApiResp.success(HttpStatus.OK, "토너먼트 시작 완료");
    }
    
    @Operation(summary = "토너먼트 강제 종료", description = "토너먼트를 강제로 종료합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "Not Found"),
    })
    @PostMapping("/v1/tournament/force-end")
    public ApiResp<Object> endTournament() {
        tournamentService.endLastestTournaments();
        
        return ApiResp.success(HttpStatus.OK, "토너먼트 종료 완료");
    }
}