package com.st.eighteen_be.user.scheduler;

import com.st.eighteen_be.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * packageName    : com.st.eighteen_be.user.scheduler
 * fileName       : UserInfoScheduler
 * author         : ipeac
 * date           : 24. 12. 23.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 12. 23.        ipeac       최초 생성
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class UserInfoScheduler {
    private final UserService userService;
    // 1분마다 동작
    @Scheduled(cron = "0 0/1 * * * ?")
    public void updateUserInfoToRedis() {
        //Redis 에 유저 데이터를 올린다.
        // 1분 전에 업데이트된 내역이 있는지 확인하여 업데이트
        userService.updateUserInfoToRedis();
        
        log.info("like count update complete");
    }
    
}
