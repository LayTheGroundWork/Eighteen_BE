package com.st.eighteen_be.tournament.scheduler;

import com.st.eighteen_be.common.annotation.ServiceWithRedisTest;
import com.st.eighteen_be.common.extension.RedisTestContainerExtenstion;
import com.st.eighteen_be.tournament.domain.redishash.RandomUser;
import com.st.eighteen_be.tournament.service.TournamentService;
import com.st.eighteen_be.user.dto.response.UserRandomResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * packageName    : com.st.eighteen_be.tournament.batch
 * fileName       : TournamentBatchTest
 * author         : ipeac
 * date           : 24. 5. 19.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 5. 19.        ipeac       최초 생성
 */
@DisplayName("토너먼트 배치 테스트")
@ServiceWithRedisTest
@ExtendWith(MockitoExtension.class)
public class TournamentBatchTest extends RedisTestContainerExtenstion {
    private TournamentScheduler tournamentScheduler;

    @Autowired
    private RedisTemplate<String, RandomUser> redisTemplate;

    @MockBean
    private TournamentService tournamentService;

    @BeforeEach
    void setUp() {
        tournamentScheduler = new TournamentScheduler(tournamentService, redisTemplate);
        redisTemplate.delete("pickedRandomUser");
    }

    @Test
    @DisplayName("startNewTournaments 스케쥴러 정상 동작 테스트 - enums 길이만큼 startTournament 호출")
    void When_startNewTournaments_enumLength_Then_startTournament() {
        //when
        tournamentScheduler.startNewTournaments();

        //then
        verify(tournamentService, times(1)).startTournament();
    }

    @Test
    @DisplayName("endTournaments 스케쥴러 정상 동작 테스트")
    void When_endTournaments_Then_endLastestTournaments() {
        //when
        tournamentScheduler.endTournaments();

        //then
        verify(tournamentService, times(1)).endLastestTournaments();
    }

    @Test
    @DisplayName("pickRandomUser 스케쥴러 정상 동작 테스트")
    void When_pickRandomUser_Then_pickRandomUser() {
        // given
        List<UserRandomResponseDto> randomUsers = Collections.singletonList(
                UserRandomResponseDto.of(1, "https://example.com/profile.jpg")
        );
        when(tournamentService.pickRandomUser()).thenReturn(randomUsers);

        // when
        tournamentScheduler.pickRandomUser();

        // then
        HashOperations<String, String, RandomUser> hashOperations = redisTemplate.opsForHash();
        RandomUser storedUser = hashOperations.get("pickedRandomUser", "1");

        assertThat(storedUser).isNotNull();
        assertThat(storedUser.getUserId()).isEqualTo("1");
        assertThat(storedUser.getProfileImageUrl()).isEqualTo("https://example.com/profile.jpg");

        verify(tournamentService, times(1)).pickRandomUser();
    }
    
    @Test
    @DisplayName("pickRandomUser 스케쥴러의 경우 이미 들어간 데이터를 정상적으로 삭제시키는지 확인")
    void When_pickRandomUser_Then_deletePreviousData() {
        // given
        List<UserRandomResponseDto> randomUsers = Collections.singletonList(
                UserRandomResponseDto.of(1, "https://example.com/profile.jpg")
        );
        when(tournamentService.pickRandomUser()).thenReturn(randomUsers);
        
        RedisTemplate<String, RandomUser> spyRedisTemplate = spy(redisTemplate);
        tournamentScheduler = new TournamentScheduler(tournamentService, spyRedisTemplate);
        
        // when
        tournamentScheduler.pickRandomUser();
        
        // then
        HashOperations<String, String, RandomUser> hashOperations = spyRedisTemplate.opsForHash();
        RandomUser storedUser = hashOperations.get("pickedRandomUser", "1");
        
        assertThat(storedUser).isNotNull();
        assertThat(storedUser.getUserId()).isEqualTo("1");
        assertThat(storedUser.getProfileImageUrl()).isEqualTo("https://example.com/profile.jpg");
        
        //레디스 삭제 로직이 한번 동작했는지 확인한다.
        verify(spyRedisTemplate, times(1)).delete("pickedRandomUser");
    }}
