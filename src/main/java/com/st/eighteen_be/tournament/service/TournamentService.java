package com.st.eighteen_be.tournament.service;

import com.st.eighteen_be.tournament.domain.dto.response.TournamentSearchResponseDTO;
import com.st.eighteen_be.tournament.domain.entity.GameEntity;
import com.st.eighteen_be.tournament.domain.entity.TournamentEntity;
import com.st.eighteen_be.tournament.domain.enums.TournamentCategoryEnums;
import com.st.eighteen_be.tournament.repository.GameEntityRepository;
import com.st.eighteen_be.tournament.repository.TournamentEntityRepository;
import com.st.eighteen_be.tournament.service.helper.TournamentHelperService;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * packageName    : com.st.eighteen_be.tournament.api
 * fileName       : TournamentService
 * author         : ipeac
 * date           : 24. 5. 15.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 5. 15.        ipeac       최초 생성
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TournamentService {
    private final TournamentEntityRepository tournamentEntityRepository;
    private final GameEntityRepository gameEntityRepository;

    public List<TournamentSearchResponseDTO> search(PageRequest pageRequest, TournamentCategoryEnums category) {

        return tournamentEntityRepository.findTournamentEntityByCategory(category, pageRequest).stream()
                .map(TournamentEntity::toTournamentSearchResponseDTO)
                .toList();
    }

    @Transactional(readOnly = false)
    public void startTournament(@Nonnull TournamentCategoryEnums category) {
        TournamentEntity created = TournamentEntity.createTournamentEntity(category);

        TournamentEntity savedTournament = tournamentEntityRepository.save(created);

        //TODO : 토너먼트 참가자를 랜덤으로 선정한다.
        List<String> users = TournamentHelperService.pickRandomUser();

        createSixteenthRoundGames(savedTournament, users);
    }

    @Transactional(readOnly = false)
    public void createSixteenthRoundGames(@Nonnull TournamentEntity tournament, @Nonnull List<String> users) {
        TournamentHelperService.checkSixteenthRoundUserCount(users);

        List<GameEntity> games = new ArrayList<>();

        for (int i = 0; i < users.size(); i += 2) {
            GameEntity game = GameEntity.createSixteenthRoundGame(tournament, users.get(i), users.get(i + 1));
            games.add(game);
        }

        gameEntityRepository.saveAll(games);
    }

    public void endLastTournament() {
        //마지막 토너먼트를 종료시킨다.

        //승자를 선정한다.
    }
}