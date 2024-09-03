package com.st.eighteen_be.user.api;

import com.st.eighteen_be.user.service.MyPageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(name = "마이페이지 API", description = "마이페이지 API")
@RestController
@RequiredArgsConstructor
@RequestMapping
public class MyPageApiController {

    private final MyPageService myPageService;

    //@Operation(summary = "")
}
