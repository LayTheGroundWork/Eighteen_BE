package com.st.eighteen_be.tournament_participant.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
@RestController
@RequestMapping("/v1/api/tournament/participant")
public class TournamentParticipantApiController {
    
    /*
    * 레디스 randomUser 카테고리에 맞게 보여준다.
    * */
    @GetMapping("/show-most-liked-user")
    public String test() {
        return "test";
    }
}
