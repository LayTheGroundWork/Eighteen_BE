package com.st.eighteen_be.tournament.service;

import com.st.eighteen_be.common.exception.ErrorCode;
import com.st.eighteen_be.common.exception.sub_exceptions.data_exceptions.BadRequestException;
import com.st.eighteen_be.common.exception.sub_exceptions.data_exceptions.NotFoundException;
import com.st.eighteen_be.tournament.domain.dto.request.TournamentVoteRequestDTO;
import com.st.eighteen_be.tournament.domain.dto.response.TournamentSearchResponseDTO;
import com.st.eighteen_be.tournament.domain.dto.response.TournamentVoteResultResponseDTO;
import com.st.eighteen_be.tournament.domain.entity.TournamentEntity;
import com.st.eighteen_be.tournament.domain.entity.TournamentParticipantEntity;
import com.st.eighteen_be.tournament.domain.enums.TournamentCategoryEnums;
import com.st.eighteen_be.tournament.domain.redishash.RandomUser;
import com.st.eighteen_be.tournament.repository.TournamentEntityRepository;
import com.st.eighteen_be.tournament.repository.TournamentParticipantRepository;
import com.st.eighteen_be.tournament.repository.VoteEntityRepository;
import com.st.eighteen_be.user.dto.response.UserRandomResponseDto;
import com.st.eighteen_be.user.repository.UserRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

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
@Slf4j
public class TournamentService {
    public static final String RANDOM_USER_KEY = "randomUser";
    private final TournamentEntityRepository tournamentEntityRepository;
    private final TournamentParticipantRepository tournamentParticipantEntityRepository;
    private final VoteEntityRepository voteEntityRepository;

    private final UserRepository userRepository;
    private final RedisTemplate<String, RandomUser> redisTemplate;

    public List<TournamentSearchResponseDTO> search(PageRequest pageRequest, TournamentCategoryEnums category) {
        log.info("search start category : {}", category);

        return tournamentEntityRepository.findTournamentByCategoryAndPaging(category, pageRequest);
    }

    @Transactional(readOnly = false)
    public void startTournament() {
        log.info("startTournament start");

        //카테고리 별로 최신 시즌 조회 후 없으면 토너먼트를 생성한다.
        findLastestTournamentsGroupByCategory();
    }

    private void findLastestTournamentsGroupByCategory() {
        log.info("findLastestTournamentsGroupByCategory start");

        for (TournamentCategoryEnums category : TournamentCategoryEnums.values()) {
            TournamentEntity lastestTournament = tournamentEntityRepository.findFirstByCategoryOrderByCreatedDateDesc(category)
                    .orElse(null);

            int newSeason = lastestTournament == null ? 1 : lastestTournament.getSeason() + 1;
            TournamentEntity newTournament = createNewTournament(category, newSeason);

            //TODO : 스케쥴러 00시 마다 돈 내역을 토대로 redis 에서 회원을 가져온다. -- 현재는 랜덤 32명의 회원에 대해 카테고리별로 분류가 되어 있지 않기에, 그냥 16명을 전부 참가자로 설정하도록 되어 있다.
            saveRandomParticipantsFromRedis(newTournament);
        }
    }

    private void saveRandomParticipantsFromRedis(TournamentEntity newTournament) {
        Objects.requireNonNull(redisTemplate.opsForHash().entries(RANDOM_USER_KEY))
                .values()
                .forEach(randomUser -> {
                    TournamentParticipantEntity participant = TournamentParticipantEntity.from(newTournament, (RandomUser) randomUser);
                    tournamentParticipantEntityRepository.save(participant);
                });
    }

    @Transactional(readOnly = false)
    public List<UserRandomResponseDto> saveRandomUser() {
        log.info("pickRandomUser start");

        // Redis에서 기존 값을 삭제합니다.
        deleteAlreadyExistRamdomUserFromRedis();

        //TODO 향후 : 참가자는 해당 카테고리에 맞는 사람들로 가져와야한다. 지금은 랜덤으로 32명을 그냥 뽑는 형식이다.
        List<UserRandomResponseDto> pickedRandomUser = userRepository.findRandomUser();

        validateRandomUserCount(pickedRandomUser);

        putRandomUserToRedis(pickedRandomUser);

        return pickedRandomUser;
    }

    private void deleteAlreadyExistRamdomUserFromRedis() {
        redisTemplate.delete(RANDOM_USER_KEY);
    }

    private void putRandomUserToRedis(List<UserRandomResponseDto> pickedRandomUser) {
        for (UserRandomResponseDto user : pickedRandomUser) {
            RandomUser randomUser = user.toRandomUser();
            redisTemplate.opsForHash().put(RANDOM_USER_KEY, randomUser.getUid(), randomUser);
        }
    }

    private static void validateRandomUserCount(List<UserRandomResponseDto> randomUser) {
        if(randomUser.size() != 32) {
            throw new BadRequestException(ErrorCode.NOT_ENOUGH_USER);
        }
    }

    @Transactional(readOnly = false)
    public TournamentEntity createNewTournament(TournamentCategoryEnums category, int season) {
        log.info("createNewTournament start category : {}", category);

        TournamentEntity created = TournamentEntity.createTournamentEntity(category, season);

        return tournamentEntityRepository.save(created);
    }

    @Transactional(readOnly = false)
    public void endLastestTournaments() {
        log.info("endLastestTournaments start");

        for (TournamentCategoryEnums category : TournamentCategoryEnums.values()) {
            TournamentEntity foundTournamet = endTournamentByCategory(category);
            determineWinner(foundTournamet.getTournamentNo());
        }
    }

    private TournamentEntity endTournamentByCategory(TournamentCategoryEnums category) {
        log.info("endTournamentByCategory start category : {}", category.getCategory());

        return tournamentEntityRepository.findFirstByCategoryAndStatusIsTrueOrderByCreatedDateDesc(category)
                .map(tournament -> {
                    tournament.endTournament();
                    return tournamentEntityRepository.save(tournament);
                })
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_CATEGORY));
    }

    @Transactional(readOnly = false)
    public List<TournamentVoteResultResponseDTO> determineWinner(@NonNull Long tournamentNo) {
        log.info("determineWinner start");

        List<TournamentVoteResultResponseDTO> voteResult = voteEntityRepository.findTournamentVoteResult(tournamentNo);

        setRank(voteResult);

        return voteResult;
    }

    private void setRank(List<TournamentVoteResultResponseDTO> voteResult) {
        long rank = 1;

        for (TournamentVoteResultResponseDTO tournamentVoteResultResponseDTO : voteResult) {
            tournamentVoteResultResponseDTO.setRank(rank++);
        }
    }

    @Transactional(readOnly = false)
    public void processVote(TournamentVoteRequestDTO voteRequestDTO) {
        log.info("processVote start");

        tournamentParticipantEntityRepository.updateVotePoints(voteRequestDTO);
        tournamentParticipantEntityRepository.insertVoteRecord(voteRequestDTO);
    }
}
