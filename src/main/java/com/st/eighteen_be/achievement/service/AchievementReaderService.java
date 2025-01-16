package com.st.eighteen_be.achievement.service;

import com.st.eighteen_be.achievement.domain.Achievement;
import com.st.eighteen_be.achievement.repository.AchievementRepository;
import com.st.eighteen_be.common.exception.ErrorCode;
import com.st.eighteen_be.common.exception.sub_exceptions.data_exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AchievementReaderService {

    private final AchievementRepository achievementRepository;

    public Achievement getDetailAchievement(Integer id) {
        return achievementRepository.findById(id).orElseThrow(
                () -> new NotFoundException(ErrorCode.NOT_FOUND_ACHIEVEMENT)
        );
    }


}
