package com.st.eighteen_be;

import com.st.eighteen_be.user.domain.SchoolData;
import com.st.eighteen_be.user.domain.UserInfo;
import com.st.eighteen_be.user.domain.UserMediaData;
import com.st.eighteen_be.user.domain.UserRoles;
import com.st.eighteen_be.user.enums.CategoryType;
import com.st.eighteen_be.user.enums.RolesType;
import com.st.eighteen_be.user.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreateTester {

    private static final String TESTER_PHONE_NUMBER = "01012345678";
    private static final String TESTER_ENCRYPT_PHONE_NUMBER = "49 87 37 -20 56 2 107 68 54 120 -37 -100 -115 119 -61 -57 ";
    private static final String TESTER_NICKNAME = "tester1";
    private static final LocalDate TESTER_BIRTHDAY = LocalDate.of(1999, 12, 23);
    private static final SchoolData TESTER_SCHOOL_DATA = new SchoolData("테스터고등학교", "서울");
    private static final String TESTER_UNIQUE_ID = "Tester";
    private static final Set<String> TESTER_ROLES = Set.of("USER");

    private static final String IMAGE_KEY = "testKey";
    private static final String THUMBNAIL_IMAGE_KEY = "thumbnail_testKey";
    private static final boolean IS_THUMBNAIL = true;

    private static final boolean TOURNAMENT_JOIN = true;


    private final UserRepository userRepository;

    @PostConstruct
    public void createTester() {
        Optional<UserInfo> existingTester = userRepository.findByUniqueId(TESTER_UNIQUE_ID);

        if (existingTester.isPresent()) {
            log.info("Tester already exists with unique ID: {}", TESTER_UNIQUE_ID);
            userRepository.deleteAll();
        }

        UserInfo tester = UserInfo.builder()
                .thumbnail(THUMBNAIL_IMAGE_KEY)
                .phoneNumber(TESTER_ENCRYPT_PHONE_NUMBER)
                .nickName(TESTER_NICKNAME)
                .birthDay(TESTER_BIRTHDAY)
                .schoolData(TESTER_SCHOOL_DATA)
                .uniqueId(TESTER_UNIQUE_ID)
                .category(CategoryType.ETC)
                .tournamentJoin(TOURNAMENT_JOIN)
                .build();

        UserRoles userRoles = UserRoles.builder()
                .role(RolesType.USER)
                .user(tester)
                .build();

        tester.addRole(userRoles);

        UserMediaData userMediaData = UserMediaData.builder()
                .imageKey(IMAGE_KEY)
                .isThumbnail(IS_THUMBNAIL)
                .user(tester)
                .build();

        tester.addMediaData(userMediaData);

        try {
            userRepository.save(tester);
            log.info("Tester created successfully: {}", tester);
        } catch (Exception e) {
            log.error("Error creating tester: ", e);
        }
    }
}