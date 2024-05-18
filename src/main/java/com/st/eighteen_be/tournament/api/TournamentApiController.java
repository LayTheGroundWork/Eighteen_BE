package com.st.eighteen_be.tournament.api;

import com.st.eighteen_be.common.response.ApiResp;
import com.st.eighteen_be.tournament.batch.TournamentBatch;
import com.st.eighteen_be.tournament.domain.dto.response.TournamentSearchResponseDTO;
import com.st.eighteen_be.tournament.service.TournamentService;
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
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/")
public class TournamentApiController {
    private final TournamentService tournamentService;

    /**
     * 분야별 토너먼트 페이징 조회
     *
     * @return ApiResp<Object>
     */
    @GetMapping("/v1/tournament/search")
    public ApiResp<List<TournamentSearchResponseDTO>> search(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "TOURNAMENT_NO") String sort,
            @RequestParam(value = "sortDirection", defaultValue = "DESC") Sort.Direction sortDirection,
            @RequestParam(value = "category", required = false) String category
    ) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(sortDirection, sort));

        List<TournamentSearchResponseDTO> responseDTOs = tournamentService.search(pageRequest, category);

        return ApiResp.success(HttpStatus.OK, responseDTOs);
    }

    /**
     * 토너먼트 참여 시작 (배치용)
     * <p>
     * {@link TournamentBatch#startNewTournaments()} ()}
     *
     * @return ApiResp<Object>
     */
    @PostMapping("/v1/tournament/start")
    public ApiResp<Object> detail() {
        tournamentService.test();
        return ApiResp.success(HttpStatus.OK, "test");
    }

    /**
     * 토너먼트 종료 (배치용)
     * <p>
     *
     * @return ApiResp<Object>
     */
    @PostMapping("/v1/tournament/end")
    public ApiResp<Object> end() {
        tournamentService.test();
        return ApiResp.success(HttpStatus.OK, "test");
    }

    @PostMapping("/v1/tournament/vote")
    public ApiResp<Object> vote() {
        tournamentService.test();
        return ApiResp.success(HttpStatus.OK, "test");
    }
}