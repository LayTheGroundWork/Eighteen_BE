package com.st.eighteen_be;

import com.st.eighteen_be.user.domain.SchoolData;
import com.st.eighteen_be.user.domain.UserInfo;
import com.st.eighteen_be.user.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreateTester {

    private static final String TESTER_PHONE_NUMBER = "01012345678";
    private static final String TESTER_NICKNAME = "tester1";
    private static final LocalDate TESTER_BIRTHDAY = LocalDate.of(1999, 12, 23);
    private static final SchoolData TESTER_SCHOOL_DATA = new SchoolData("테스터고등학교", "서울");
    private static final String TESTER_UNIQUE_ID = "@Tester";
    private static final List<String> TESTER_ROLES = List.of("USER");

    private final UserRepository userRepository;

    @PostConstruct
    public void createTester() {
        Optional<UserInfo> existingTester = userRepository.findByUniqueId(TESTER_UNIQUE_ID);

        if (existingTester.isPresent()) {
            log.info("Tester already exists with unique ID: {}", TESTER_UNIQUE_ID);
            return;
        }

        UserInfo tester = UserInfo.builder()
                .phoneNumber(TESTER_PHONE_NUMBER)
                .nickName(TESTER_NICKNAME)
                .birthDay(TESTER_BIRTHDAY)
                .schoolData(TESTER_SCHOOL_DATA)
                .uniqueId(TESTER_UNIQUE_ID)
                .roles(TESTER_ROLES)
                .build();

        try {
            userRepository.save(tester);
            log.info("Tester created successfully: {}", tester);
        } catch (Exception e) {
            log.error("Error creating tester: ", e);
        }
    }
}