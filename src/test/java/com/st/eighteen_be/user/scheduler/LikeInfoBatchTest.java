package com.st.eighteen_be.user.scheduler;

import com.st.eighteen_be.user.service.LikeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("좋아요 배치 테스트")
@ExtendWith(MockitoExtension.class)
public class LikeInfoBatchTest {

    @InjectMocks
    private LikeInfoScheduler likeInfoScheduler;

    @Mock
    private LikeService likeService;

    @Test
    @DisplayName("backupUserLikeDataToMySQL 스케쥴러 정상 동작 테스트 - enums 길이만큼 backupUserLikeDataToMySQL 호출")
    public void backup_user_like_Data_to_MySQL_Test() throws Exception {
        //when
        likeInfoScheduler.backupUserLikeDataToMySQL();

        //then
        verify(likeService, times(1)).backupUserLikeDataToMySQL();
    }

    @Test
    @DisplayName("backupLikeCountToMySQL 스케쥴러 정상 동작 테스트 - enums 길이만큼 backupLikeCountToMySQL 호출")
    public void backup_user_like_count_to_MySQL_Test() throws Exception {
        //when
        likeInfoScheduler.backupLikeCountToMySQL();

        //then
        verify(likeService, times(1)).backupLikeCountToMySQL();
    }
}