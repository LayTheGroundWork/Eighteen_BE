package com.st.eighteen_be;

import com.st.eighteen_be.user.domain.SchoolData;
import com.st.eighteen_be.user.domain.UserInfo;
import com.st.eighteen_be.user.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Component
@RequiredArgsConstructor
public class CreateTester {

    private final UserRepository userRepository;

    @PostConstruct
    public void createTester(){
        String phoneNumber = "01012345678";
        String nickName = "tester1";
        LocalDate birthDay = LocalDate.of(1999,12,23);
        SchoolData schoolData = new SchoolData("테스터고등학교", "서울");
        String uniqueId = "@Tester";
        List<String> roles = new ArrayList<>();
        roles.add("USER");

        UserInfo tester = UserInfo.builder()
                .phoneNumber(phoneNumber)
                .nickName(nickName)
                .birthDay(birthDay)
                .schoolData(schoolData)
                .uniqueId(uniqueId)
                .roles(roles)
                .build();

        userRepository.save(tester);

    }
}
