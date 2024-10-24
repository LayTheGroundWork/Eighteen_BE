package com.st.eighteen_be.tournament.service;

import com.st.eighteen_be.common.annotation.ServiceWithRedisTest;
import com.st.eighteen_be.common.extension.RedisTestContainerExtenstion;
import com.st.eighteen_be.tournament.domain.redishash.MostLikedUserRedisHash;
import com.st.eighteen_be.tournament.repository.MostLikedUserRepository;
import com.st.eighteen_be.tournament.repository.TournamentEntityRepository;
import com.st.eighteen_be.tournament.repository.TournamentParticipantRepository;
import com.st.eighteen_be.tournament.repository.VoteEntityRepository;
import com.st.eighteen_be.tournament_winner.repository.TournamentWinnerRepository;
import com.st.eighteen_be.user.dto.response.MostLikedUserResponseDto;
import com.st.eighteen_be.user.repository.UserRepository;
import com.st.eighteen_be.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;


/**
 * packageName    : com.st.eighteen_be.tournament.service
 * fileName       : TournamentServiceRedisTest
 * author         : ipeac
 * date           : 24. 8. 31.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 8. 31.        ipeac       최초 생성
 */
@ServiceWithRedisTest
@DisplayName("토너먼트 서비스 레디스 테스트")
public class TournamentServiceRedisTest extends RedisTestContainerExtenstion {

    public static final String RANDOM_USER = "mostLikedUser";

    @Autowired
    private RedisTemplate<String, MostLikedUserRedisHash> redisTemplate;

    private TournamentService tournamentService;

    @MockBean
    private TournamentEntityRepository tournamentEntityRepository;

    @MockBean
    private TournamentParticipantRepository tournamentParticipantRepository;

    @MockBean
    private VoteEntityRepository voteEntityRepository;

    @MockBean
    private UserService userService;
    
    @MockBean
    private UserRepository userRepository;
    
    @MockBean
    private TournamentWinnerRepository tournamentWinnerRepository;
    
    @Autowired
    private MostLikedUserRepository mostLikedUserRepository;

    @BeforeEach
    void setUp() {
        tournamentService = new TournamentService(userService, tournamentEntityRepository, tournamentParticipantRepository, tournamentWinnerRepository, voteEntityRepository, userRepository, mostLikedUserRepository, redisTemplate);
    }

    @Test
    @DisplayName("랜덤 유저 선택 레디스 저장 확인")
    void When_saveRandomUser_Then_saveMostLikedUsersToRedis() {
        //given
        List<MostLikedUserResponseDto> mostLikedUserResponseDtos = new ArrayList<>();
        for (int i = 1; i <= 32; i++) {
            mostLikedUserResponseDtos.add(MostLikedUserResponseDto.of("userId" + i, "http://test.com"));
        }
        
        given(userRepository.findRandomUsers(any(),anyInt())).willReturn(mostLikedUserResponseDtos);

        //when
        tournamentService.saveMostLikedUsersToRedis();

        //then
        //userRandomResponseDtos 32개가 redis 에 저장되었는지 확인
        redisTemplate.opsForHash().entries(RANDOM_USER).forEach((k, v) -> {
            MostLikedUserRedisHash mostLikedUserRedisHash = (MostLikedUserRedisHash) v;

            //randomUser userId는  userId1 ~ 32 사이의 값이어야 함
            assertThat(mostLikedUserRedisHash.getUserId()).isEqualTo(k);
            assertThat(mostLikedUserRedisHash.getProfileImageUrl()).isEqualTo("http://test.com");
        });
    }
}
