package com.st.eighteen_be;

import com.st.eighteen_be.user.domain.SchoolData;
import com.st.eighteen_be.user.domain.UserInfo;
import com.st.eighteen_be.user.domain.UserMediaData;
import com.st.eighteen_be.user.domain.UserRoles;
import com.st.eighteen_be.user.enums.CategoryType;
import com.st.eighteen_be.user.enums.RolesType;
import com.st.eighteen_be.user.repository.UserRepository;
import com.st.eighteen_be.user.service.EncryptService;
import jakarta.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreateTester {

    private static final int TESTER_COUNT = 32;
    private static final String BASE_PHONE_NUMBER = "01000000000";
    private static final String BASE_NICKNAME = "user_";
    private static final String BASE_UNIQUE_ID = "tester";

    private static final List<String> SCHOOLS = Arrays.asList(
            "서울고등학교", "부산고등학교", "대구고등학교",
            "인천고등학교", "광주고등학교", "대전고등학교",
            "울산고등학교", "경기도고등학교", "강원도고등학교"
    );

    private final UserRepository userRepository;
    private final EncryptService encryptService;

    @PostConstruct
    public void createTesters() {
        // 기존 데이터 삭제
        userRepository.deleteAll();

        for (int i = 0; i < TESTER_COUNT; i++) {
            String phoneNumber = generatePhoneNumber(i+1);
            String nickName = BASE_NICKNAME + (i + 1);
            LocalDate birthDay = generateRandomBirthday();
            String uniqueId = BASE_UNIQUE_ID + (i + 1);
            SchoolData schoolData = new SchoolData(randomSchool(i%SCHOOLS.size()), "지역");

            String encryptNumber = encryptService.encryptPhoneNumber(phoneNumber);

            UserInfo tester = UserInfo.builder()
                    .thumbnail("thumbnail_testKey")
                    .phoneNumber(encryptNumber)
                    .nickName(nickName)
                    .birthDay(birthDay)
                    .schoolData(schoolData)
                    .uniqueId(uniqueId)
                    .category(CategoryType.ETC)
                    .tournamentJoin(true)
                    .build();

            UserRoles userRoles = UserRoles.builder()
                    .role(RolesType.USER)
                    .build();
            userRoles.setUser(tester);

            UserMediaData userMediaData = UserMediaData.builder()
                    .imageKey("testKey")
                    .isThumbnail(true)
                    .build();
            userMediaData.setUser(tester);

            try {
                userRepository.save(tester);
                log.info("Tester created successfully: phone:{} / uniqueId:{}", phoneNumber,tester.getUniqueId());
            } catch (Exception e) {
                log.error("Error creating tester: ", e);
            }
        }
    }

    private String generatePhoneNumber(int index) {
        return BASE_PHONE_NUMBER.substring(0, BASE_PHONE_NUMBER.length() - 2) + String.format("%02d", index);
    }

    private LocalDate generateRandomBirthday() {
        int year = 2004 + (int) (Math.random() * 4); // 2004~2007
        int month = 1 + (int) (Math.random() * 12); // 1~12
        int day = 1 + (int) (Math.random() * 28); // 1~28
        return LocalDate.of(year, month, day);
    }

    private String randomSchool(int random) {
        return SCHOOLS.get(random);
    }
}
