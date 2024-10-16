package com.st.eighteen_be.user.scheduler;

import com.st.eighteen_be.user.service.LikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * packageName    : com.st.eighteen_be.user.scheduler
 * fileName       : LikeInfoScheduler
 * author         : ehgur
 * date           : 2024-07-16
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-07-16        ehgur             최초 생성
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeInfoScheduler {

    private final LikeService likeService;

    @Scheduled(cron = "0 0 2 * * ?")
    public void backupDataToMySQL(){
        likeService.backupUserLikeDataToMySQL();
        log.info("user likes update complete");

        likeService.backupLikeCountToMySQL();
        log.info("user like count update complete");
    }
}
