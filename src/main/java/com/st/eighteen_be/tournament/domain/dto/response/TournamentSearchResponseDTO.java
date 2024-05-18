package com.st.eighteen_be.tournament.domain.dto.response;

import lombok.*;

/**
 * packageName    : com.st.eighteen_be.tournament.api
 * fileName       : TournamentSearchResponseDTO
 * author         : ipeac
 * date           : 24. 5. 18.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 5. 18.        ipeac       최초 생성
 */
@Getter
@Setter
@Builder
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor
public class TournamentSearchResponseDTO {
    private Long tournamentNo;
    private String tournamentThumbnailUrl;
}