package com.st.eighteen_be.config.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

/**
 * packageName    : com.st.eighteen_be.config.scheduler
 * fileName       : ScheduleConfig
 * author         : ipeac
 * date           : 24. 5. 18.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 5. 18.        ipeac       최초 생성
 */
@Configuration
@EnableAsync
@EnableScheduling
@Slf4j
public class ScheduleConfig implements SchedulingConfigurer {
    @Bean
    public ThreadPoolTaskScheduler configureTasks() {
        log.info("configureTasks start");
        
        ThreadPoolTaskScheduler taskExecutor = new ThreadPoolTaskScheduler();
        
        taskExecutor.setPoolSize(3);
        taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        taskExecutor.setAwaitTerminationSeconds(20);
        taskExecutor.setThreadNamePrefix("scheduled-task-");
        
        log.info("configureTasks end");
        
        return taskExecutor;
    }
    
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        log.info("configureTasks start taskRegistrar : {}", taskRegistrar);
        
        taskRegistrar.setTaskScheduler(configureTasks());
        
        log.info("configureTasks end taskRegistrar : {}", taskRegistrar);
    }
}