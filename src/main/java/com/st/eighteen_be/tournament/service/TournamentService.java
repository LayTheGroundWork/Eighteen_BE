package com.st.eighteen_be.tournament.service;

import com.st.eighteen_be.achievement.domain.Achievement;
import com.st.eighteen_be.achievement.repository.AchievementRepository;
import com.st.eighteen_be.common.exception.ErrorCode;
import com.st.eighteen_be.common.exception.sub_exceptions.data_exceptions.BadRequestException;
import com.st.eighteen_be.common.exception.sub_exceptions.data_exceptions.NotFoundException;
import com.st.eighteen_be.tournament.constants.TournamentConstants;
import com.st.eighteen_be.tournament.domain.dto.request.TournamentVoteRequestDTO;
import com.st.eighteen_be.tournament.domain.dto.response.TournamentSearchResponseDTO;
import com.st.eighteen_be.tournament.domain.dto.response.TournamentVoteResultResponseDTO;
import com.st.eighteen_be.tournament.domain.entity.TournamentEntity;
import com.st.eighteen_be.tournament.domain.entity.TournamentParticipantEntity;
import com.st.eighteen_be.tournament.domain.enums.TournamentCategoryType;
import com.st.eighteen_be.tournament.domain.redishash.MostLikedUserRedisHash;
import com.st.eighteen_be.tournament.repository.MostLikedUserRepository;
import com.st.eighteen_be.tournament.repository.TournamentEntityRepository;
import com.st.eighteen_be.tournament.repository.TournamentParticipantRepository;
import com.st.eighteen_be.tournament.repository.VoteEntityRepository;
import com.st.eighteen_be.tournament_winner.repository.TournamentWinnerRepository;
import com.st.eighteen_be.user.domain.UserInfo;
import com.st.eighteen_be.user.dto.response.MostLikedUserResponseDto;
import com.st.eighteen_be.user.enums.CategoryType;
import com.st.eighteen_be.user.repository.UserRepository;
import com.st.eighteen_be.user.service.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

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
    private final UserService userService;

    private final TournamentEntityRepository tournamentEntityRepository;
    private final TournamentParticipantRepository tournamentParticipantEntityRepository;
    private final TournamentWinnerRepository tournamentWinnerRepository;
    private final VoteEntityRepository voteEntityRepository;
    private final AchievementRepository achievementRepository;
    
    private final UserRepository userRepository;
    
    private final MostLikedUserRepository mostLikedUserRepository;
    private final RedisTemplate<String, MostLikedUserRedisHash> redisTemplate;
    
    public List<TournamentSearchResponseDTO> search() {
        List<TournamentSearchResponseDTO> tournamentMainInfos = tournamentEntityRepository.findTournamentMainInfos();
        
        //없는 카테고리의 경우에는 우승자는 빈값으로 주고으로 데이터를 주려고함 -- 굳이 쿼리 한방으로 처리하지않아도 된다.
        addEmptyWinnerCategories(tournamentMainInfos);
        
        //토너먼트 카테고리별 max id 조회 후 삽입처리한다.
        setMaxTournamentNumberIn(tournamentMainInfos);
        
        return tournamentMainInfos;
    }
    
    private void setMaxTournamentNumberIn(List<TournamentSearchResponseDTO> tournamentMainInfos) {
        for (TournamentSearchResponseDTO tournamentMainInfo : tournamentMainInfos) {
            Long maxTournamentNo = tournamentParticipantEntityRepository.getMaxTournamentNo(TournamentCategoryType.of(tournamentMainInfo.getCategory()));
            
            tournamentMainInfo.setThisWeekTournamentNo(maxTournamentNo);
        }
    }
    
    private static void addEmptyWinnerCategories(List<TournamentSearchResponseDTO> tournamentMainInfos) {
        for (TournamentCategoryType category : TournamentCategoryType.values()) {
            boolean isExist = tournamentMainInfos.stream()
                    .anyMatch(tournamentSearchResponseDTO -> Objects.equals(tournamentSearchResponseDTO.getCategory(), category.getCategory()));
            
            if (!isExist) {
                tournamentMainInfos.add(new TournamentSearchResponseDTO(category, new ArrayList<>()));
            }
        }
    }
    
    @Transactional(readOnly = false)
    public void startTournament() {
        log.info("startTournament start");

        //카테고리 별로 최신 시즌 조회 후 없으면 토너먼트를 생성한다.
        findLastestTournamentsGroupByCategory();
    }

    private void findLastestTournamentsGroupByCategory() {
        log.info("findLastestTournamentsGroupByCategory start");

        for (TournamentCategoryType category : TournamentCategoryType.values()) {
            TournamentEntity lastestTournament = tournamentEntityRepository.findFirstByCategoryOrderByCreatedDateDesc(category)
                    .orElse(null);

            int newSeason = lastestTournament == null ? 1 : lastestTournament.getSeason() + 1;
            TournamentEntity newTournament = createNewTournament(category, newSeason);

            //TODO : 스케쥴러 00시 마다 돈 내역을 토대로 redis 에서 회원을 가져온다.
            saveMostLikedParticipantsFromRedis(newTournament, category);
        }
    }

    private void saveMostLikedParticipantsFromRedis(TournamentEntity newTournament, TournamentCategoryType category) {
        String categoryKey = String.format(TournamentConstants.MOST_LIKED_USER_KEY + ":%s", category.getCategory());

        // 레디스로 해시테이블 조회해서  참가자 목록 생성 -> 리스트로 들고와야함
        HashOperations<String, String, MostLikedUserRedisHash> hashOperations = redisTemplate.opsForHash();

        List<TournamentParticipantEntity> participants = hashOperations.values(categoryKey).stream().map(randomUser -> randomUser.from(newTournament)).toList();

        tournamentParticipantEntityRepository.saveAll(participants);
    }

    @Transactional(readOnly = false)
    public Set<MostLikedUserResponseDto> saveMostLikedUsersToRedis() {
        // Redis에서 기존 값을 삭제합니다.
        deleteAlreadyExistMostLikedUserFromRedis();

        Set<MostLikedUserResponseDto> showedMostLikedUsers = new HashSet<>();

        for (CategoryType category : CategoryType.values()) {
            //저번주의 데이터에 대해 좋아요 순으로 16명을 뽑는다 16명이 안찰수도 있다.
            //현재 날짜 기준으로 저번주 월요일 설정값을 매개변수로 넣고 저번주 일요일을 매개변수로 넣어준다.
            LocalDateTime lastweekMonday = LocalDate.now()
                    .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                    .minusWeeks(1).atStartOfDay();
            
            //저번주 일요일
            LocalDateTime lastweekSunday = LocalDate.now()
                    .with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
                    .atTime(LocalTime.MAX);
            
            //16명 제한으로 좋아요순으로 호출한다.
            List<MostLikedUserResponseDto> selectedUsers = userRepository.findUsersByCategoryOrderByLastweekLikeCountLimit16(category, lastweekMonday, lastweekSunday);
            
            //16명 초과하는 경우 16명으로 제한함
            selectedUsers = subtractOverSixteenUsers(selectedUsers);
            
            //좋아요 있는 유저의 경우 랜덤으로 넣는다 (16 - 좋아요 있는 유저)
            fillLeftUsersByRandom(category, selectedUsers);
            
            //TODO 일단 주석처리 -> 검증이 필요한지 생각해봐야할듯.
            // validateRandomUserCount(pickedRandomUser);

            //레디스에 토너먼트 후보 유저를 올린다.
            putMostLikedUserToRedis(selectedUsers, category);

            showedMostLikedUsers.addAll(selectedUsers);
        }

        return showedMostLikedUsers;
    }
    
    private void fillLeftUsersByRandom(CategoryType category, List<MostLikedUserResponseDto> selectedUsers) {
        final int leftUserCount = TournamentConstants.TOURNAMENT_LIMIT_USER_COUNT - selectedUsers.size();
        if (leftUserCount > 0) {
            List<MostLikedUserResponseDto> pickedMostLikedUsers = userRepository.findRandomUsers(category, leftUserCount);
            selectedUsers.addAll(pickedMostLikedUsers);
        }
    }
    
    @NotNull
    private static List<MostLikedUserResponseDto> subtractOverSixteenUsers(List<MostLikedUserResponseDto> selectedUsers) {
        //selectUsers 가 16명을 초과하는 경우 16명으로 제한함
        if (selectedUsers.size() > TournamentConstants.TOURNAMENT_LIMIT_USER_COUNT) {
            selectedUsers = selectedUsers.subList(0, TournamentConstants.TOURNAMENT_LIMIT_USER_COUNT);
        }
        
        return selectedUsers;
    }
    
    private void deleteAlreadyExistMostLikedUserFromRedis() {
        mostLikedUserRepository.deleteAll();
    }

    private void putMostLikedUserToRedis(List<MostLikedUserResponseDto> pickedMostLikedUsers, CategoryType category) {
        String categoryKey = String.format(TournamentConstants.MOST_LIKED_USER_KEY +":%s", category.getCategory());

        for (MostLikedUserResponseDto user : pickedMostLikedUsers) {
            MostLikedUserRedisHash mostLikedUserRedisHash = user.toMostLikedHash(categoryKey);
            redisTemplate.opsForHash().put(categoryKey, mostLikedUserRedisHash.getUserId(), mostLikedUserRedisHash);
        }
    }

    private static void validateRandomUserCount(List<MostLikedUserResponseDto> randomUser) {
        if(randomUser.size() != TournamentConstants.TOURNAMENT_LIMIT_USER_COUNT) {
            throw new BadRequestException(ErrorCode.NOT_ENOUGH_USER);
        }
    }

    @Transactional(readOnly = false)
    public TournamentEntity createNewTournament(TournamentCategoryType category, int season) {
        TournamentEntity created = TournamentEntity.createTournamentEntity(category, season);

        return tournamentEntityRepository.save(created);
    }

    @Transactional(readOnly = false)
    public void endLastestTournaments() {
        for (TournamentCategoryType category : TournamentCategoryType.values()) {
            TournamentEntity foundTournamet = endTournamentByCategory(category);
            determineWinner(foundTournamet.getTournamentNo());
        }
    }
    
    /**
     *  종료된 토너먼트 정보 조회 및 종료 프로세스
     *
     * @param category  카테고리
     * @return 종료된 토너먼트 정보
     */
    private TournamentEntity endTournamentByCategory(TournamentCategoryType category) {
        log.info("endTournamentByCategory start category : {}", category.getCategory());

        return tournamentEntityRepository.findFirstByCategoryAndStatusIsTrueOrderByCreatedDateDesc(category)
                .map(tournament -> {
                    tournament.endTournament();
                    TournamentEntity endedTournament = tournamentEntityRepository.save(tournament);
                    
                    addWinner(endedTournament);
                    
                    return endedTournament;
                })
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_CATEGORY));
    }
    
    /**
     *  종료된 토너먼트의 우승자 카운트 DB 저장
     *
     * @param endedTournament 종료된 토너먼트 정보
     */
    private void addWinner(TournamentEntity endedTournament) {
        // 종료된 토너먼트의 우승자 카운트 DB 저장
        // 0점인 경우는 우승자로 선정하지 않음
        Optional<TournamentParticipantEntity> winner = tournamentParticipantEntityRepository.findByTournament(endedTournament).stream()
                .filter(TournamentParticipantEntity::isNotZeroScore)
                .max(Comparator.comparing(TournamentParticipantEntity::getScore));
        
        winner.ifPresent(participantEntity -> {
            //우승자가 첫번쨰 토너먼트 우승이면 업적 지급
            giveAchievementToFirstWinner(participantEntity);
            tournamentWinnerRepository.save(participantEntity.toTournamentWinnerEntity(endedTournament));
        });
    }
    
    private void giveAchievementToFirstWinner(TournamentParticipantEntity participantEntity) {
        if(tournamentWinnerRepository.existsByUserId(participantEntity.getUserId())) {
            return;
        }
        
        achievementRepository.save(Achievement.builder().build());
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
        
        //VoteEntity 에서  {"TOURNAMENT_NO", "PARTICIPANT_NO", "VOTER_ID"} 가 Unique 해야함 -> 중복 투표 방지
        if (voteEntityRepository.existsByTournament_TournamentNoAndVoterId(voteRequestDTO.getTournamentNo(), loginedUser.getUniqueId())) {
            throw new BadRequestException(ErrorCode.ALREADY_VOTED);
        }
        
        tournamentParticipantEntityRepository.updateVotePoints(voteRequestDTO, loginedUser.getUniqueId());
        tournamentParticipantEntityRepository.insertVoteRecord(voteRequestDTO, loginedUser.getUniqueId());
    }
    
    public List<TournamentEntity> findAllTournaments() {
        return tournamentEntityRepository.findAll();
    }
}
