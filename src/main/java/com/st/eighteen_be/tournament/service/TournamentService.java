package com.st.eighteen_be.tournament.service;

import com.st.eighteen_be.common.exception.ErrorCode;
import com.st.eighteen_be.common.exception.sub_exceptions.data_exceptions.BadRequestException;
import com.st.eighteen_be.common.exception.sub_exceptions.data_exceptions.NotFoundException;
import com.st.eighteen_be.tournament.domain.dto.request.TournamentVoteRequestDTO;
import com.st.eighteen_be.tournament.domain.dto.response.TournamentSearchResponseDTO;
import com.st.eighteen_be.tournament.domain.dto.response.TournamentVoteResultResponseDTO;
import com.st.eighteen_be.tournament.domain.entity.TournamentEntity;
import com.st.eighteen_be.tournament.domain.entity.TournamentParticipantEntity;
import com.st.eighteen_be.tournament.domain.redishash.RandomUser;
import com.st.eighteen_be.tournament.repository.RandomUserRedisRepository;
import com.st.eighteen_be.tournament.repository.TournamentEntityRepository;
import com.st.eighteen_be.tournament.repository.TournamentParticipantRepository;
import com.st.eighteen_be.tournament.repository.VoteEntityRepository;
import com.st.eighteen_be.user.domain.UserInfo;
import com.st.eighteen_be.user.dto.response.UserRandomResponseDto;
import com.st.eighteen_be.user.enums.CategoryType;
import com.st.eighteen_be.user.repository.UserRepository;
import com.st.eighteen_be.user.service.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    private final UserService userService;

    private final TournamentEntityRepository tournamentEntityRepository;
    private final TournamentParticipantRepository tournamentParticipantEntityRepository;
    private final VoteEntityRepository voteEntityRepository;

    private final UserRepository userRepository;

    private final RandomUserRedisRepository randomUserRedisRepository;
    private final RedisTemplate<String, RandomUser> redisTemplate;

    public List<TournamentSearchResponseDTO> search(PageRequest pageRequest, CategoryType category) {
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

        for (CategoryType category : CategoryType.values()) {
            TournamentEntity lastestTournament = tournamentEntityRepository.findFirstByCategoryOrderByCreatedDateDesc(category)
                    .orElse(null);

            int newSeason = lastestTournament == null ? 1 : lastestTournament.getSeason() + 1;
            TournamentEntity newTournament = createNewTournament(category, newSeason);

            //TODO : 스케쥴러 00시 마다 돈 내역을 토대로 redis 에서 회원을 가져온다.
            saveRandomParticipantsFromRedis(newTournament, category);
        }
    }

    private void saveRandomParticipantsFromRedis(TournamentEntity newTournament, CategoryType category) {
        String categoryKey = String.format(RANDOM_USER_KEY + ":%s", category.getCategory());

        // 레디스로 해시테이블 조회해서  참가자 목록 생성 -> 리스트로 들고와야함
        HashOperations<String, String, RandomUser> hashOperations = redisTemplate.opsForHash();

        List<TournamentParticipantEntity> participants = hashOperations.values(categoryKey).stream().map(randomUser -> randomUser.from(newTournament)).toList();

        tournamentParticipantEntityRepository.saveAll(participants);
    }

    @Transactional(readOnly = false)
    public Set<UserRandomResponseDto> saveRandomUser() {
        // Redis에서 기존 값을 삭제합니다.
        deleteAlreadyExistRamdomUserFromRedis();

        Set<UserRandomResponseDto> showedRandomUser = new HashSet<>();

        for (CategoryType category : CategoryType.values()) {
            List<UserRandomResponseDto> pickedRandomUser = userRepository.findRandomUser(category);

            //TODO 일단 주석처리 -> 검증이 필요한지 생각해봐야할듯.
            // validateRandomUserCount(pickedRandomUser);

            putRandomUserToRedis(pickedRandomUser, category);

            showedRandomUser.addAll(pickedRandomUser);
        }

        return showedRandomUser;
    }

    private void deleteAlreadyExistRamdomUserFromRedis() {
        randomUserRedisRepository.deleteAll();
    }

    private void putRandomUserToRedis(List<UserRandomResponseDto> pickedRandomUser, CategoryType category) {
        String categoryKey = String.format(RANDOM_USER_KEY +":%s", category.getCategory());

        for (UserRandomResponseDto user : pickedRandomUser) {
            RandomUser randomUser = user.toRandomUser(categoryKey);
            redisTemplate.opsForHash().put(categoryKey, randomUser.getUserId(), randomUser);
        }
    }

    private static void validateRandomUserCount(List<UserRandomResponseDto> randomUser) {
        if(randomUser.size() != 32) {
            throw new BadRequestException(ErrorCode.NOT_ENOUGH_USER);
        }
    }

    @Transactional(readOnly = false)
    public TournamentEntity createNewTournament(CategoryType category, int season) {
        log.info("createNewTournament start category : {}", category);

        TournamentEntity created = TournamentEntity.createTournamentEntity(category, season);

        return tournamentEntityRepository.save(created);
    }

    @Transactional(readOnly = false)
    public void endLastestTournaments() {
        log.info("endLastestTournaments start");

        for (CategoryType category : CategoryType.values()) {
            TournamentEntity foundTournamet = endTournamentByCategory(category);
            determineWinner(foundTournamet.getTournamentNo());
        }
    }

    private TournamentEntity endTournamentByCategory(CategoryType category) {
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
    public void processVote(TournamentVoteRequestDTO voteRequestDTO, String uniqueId) {
        log.info("processVote start");

        UserInfo loginedUser = userService.findByUniqueId(uniqueId);

        tournamentParticipantEntityRepository.updateVotePoints(voteRequestDTO, loginedUser.getUniqueId());
        tournamentParticipantEntityRepository.insertVoteRecord(voteRequestDTO, loginedUser.getUniqueId());
    }
}
