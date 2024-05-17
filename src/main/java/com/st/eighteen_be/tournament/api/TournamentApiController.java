package com.st.eighteen_be.tournament.api;

import com.st.eighteen_be.common.response.ApiResp;
import com.st.eighteen_be.tournament.service.TournamentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
     * 분야 시즌
     * @return ApiResp<Object>
     */
    @GetMapping("/v1/tournament/all")
    public ApiResp<Object> findAllTournament() {
        tournamentService.test();
        return ApiResp.success();
    }
}