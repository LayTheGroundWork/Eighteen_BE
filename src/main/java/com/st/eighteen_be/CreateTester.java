package com.st.eighteen_be;

import com.st.eighteen_be.user.domain.SchoolData;
import com.st.eighteen_be.user.domain.UserInfo;
import com.st.eighteen_be.user.domain.UserMediaData;
import com.st.eighteen_be.user.domain.UserRoles;
import com.st.eighteen_be.user.enums.CategoryType;
import com.st.eighteen_be.user.enums.RolesType;
import com.st.eighteen_be.user.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreateTester {

    private static final List<String> TESTER_PHONE_NUMBER = List.of("01012345678","01012341234");
    private static final List<String> TESTER_ENCRYPT_PHONE_NUMBER = List.of(
            "49 87 37 -20 56 2 107 68 54 120 -37 -100 -115 119 -61 -57 ",
            "59 7 73 93 23 30 -3 82 -33 -114 -65 12 -62 -56 66 13 "
    );
    private static final List<String> TESTER_NICKNAME = List.of("kim","lee");
    private static final List<LocalDate> TESTER_BIRTHDAY = List.of(
            LocalDate.of(2010, 12, 23),
            LocalDate.of(2013, 12, 6)
    );
    private static final List<SchoolData> TESTER_SCHOOL_DATA = List.of(
            new SchoolData("테스터고등학교", "서울"),
            new SchoolData("테스터중학교", "정선")
    );
    private static final List<String> TESTER_UNIQUE_ID = List.of("tester1","tester2");
    private static final String IMAGE_KEY = "testKey";
    private static final String THUMBNAIL_IMAGE_KEY = "thumbnail_testKey";
    private static final boolean IS_THUMBNAIL = true;

    private static final boolean TOURNAMENT_JOIN = true;


    private final UserRepository userRepository;

    @PostConstruct
    public void createTester() {
        Optional<UserInfo> user1 = userRepository.findByUniqueId(TESTER_UNIQUE_ID.get(0));
        Optional<UserInfo> user2 = userRepository.findByUniqueId(TESTER_UNIQUE_ID.get(1));

        if(user1.isPresent() || user2.isPresent()){
            userRepository.deleteAll();
        }

        UserInfo tester1 = UserInfo.builder()
                .thumbnail(THUMBNAIL_IMAGE_KEY)
                .phoneNumber(TESTER_ENCRYPT_PHONE_NUMBER.get(0))
                .nickName(TESTER_NICKNAME.get(0))
                .birthDay(TESTER_BIRTHDAY.get(0))
                .schoolData(TESTER_SCHOOL_DATA.get(0))
                .uniqueId(TESTER_UNIQUE_ID.get(0))
                .category(CategoryType.ETC)
                .tournamentJoin(TOURNAMENT_JOIN)
                .build();

        UserInfo tester2 = UserInfo.builder()
                .thumbnail(THUMBNAIL_IMAGE_KEY)
                .phoneNumber(TESTER_ENCRYPT_PHONE_NUMBER.get(1))
                .nickName(TESTER_NICKNAME.get(1))
                .birthDay(TESTER_BIRTHDAY.get(1))
                .schoolData(TESTER_SCHOOL_DATA.get(1))
                .uniqueId(TESTER_UNIQUE_ID.get(1))
                .category(CategoryType.ETC)
                .tournamentJoin(TOURNAMENT_JOIN)
                .build();

        UserRoles userRoles1 = UserRoles.builder()
                .role(RolesType.USER)
                .build();
        userRoles1.setUser(tester1);

        UserRoles userRoles2 = UserRoles.builder()
                .role(RolesType.USER)
                .build();
        userRoles2.setUser(tester2);

        UserMediaData userMediaData1 = UserMediaData.builder()
                .imageKey(IMAGE_KEY)
                .isThumbnail(IS_THUMBNAIL)
                .build();

        userMediaData1.setUser(tester1);

        UserMediaData userMediaData2 = UserMediaData.builder()
                .imageKey(IMAGE_KEY)
                .isThumbnail(IS_THUMBNAIL)
                .build();
        userMediaData2.setUser(tester2);

        try {
            userRepository.save(tester1);
            log.info("Tester created successfully: {}", tester1);
            userRepository.save(tester2);
            log.info("Tester created successfully: {}", tester2);
        } catch (Exception e) {
            log.error("Error creating testers: ", e);
        }
    }
}