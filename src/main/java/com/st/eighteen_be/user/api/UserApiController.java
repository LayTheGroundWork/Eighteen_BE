package com.st.eighteen_be.user.api;

import com.st.eighteen_be.common.exception.ErrorCode;
import com.st.eighteen_be.common.response.ApiResp;
import com.st.eighteen_be.common.security.SecurityUtil;
import com.st.eighteen_be.jwt.JwtTokenDto;
import com.st.eighteen_be.user.domain.UserInfo;
import com.st.eighteen_be.user.dto.request.SignInRequestDto;
import com.st.eighteen_be.user.dto.request.SignUpRequestDto;
import com.st.eighteen_be.user.dto.response.UserDetailsResponseDto;
import com.st.eighteen_be.user.dto.response.UserProfileResponseDto;
import com.st.eighteen_be.user.service.LikeService;
import com.st.eighteen_be.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.st.eighteen_be.jwt.JwtTokenProvider.*;

/**
 * packageName    : com.st.eighteen_be.member.api
 * fileName       : MemberApiController
 * author         : ehgur
 * date           : 2024-04-18
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-04-18        ehgur             최초 생성
 */

@Slf4j
@Tag(name = "유저 API", description = "유저 API")
@RestController
@RequiredArgsConstructor
@RequestMapping
public class UserApiController {

    private final UserService userService;
    private final LikeService likeService;

    @Operation(summary = "회원가입", description = "회원가입")
    @PostMapping("/v1/api/user/sign-up")
    public ApiResp<UserInfo> signUp(@Valid @RequestBody SignUpRequestDto requestDto){

        return ApiResp.success(HttpStatus.OK, userService.save(requestDto));
    }

    @Operation(summary = "로그인", description = "로그인")
    @PostMapping("/v1/api/user/sign-in")
    public ApiResp<JwtTokenDto> signIn(@Valid @RequestBody SignInRequestDto requestDto, HttpServletResponse response) {

        JwtTokenDto jwtTokenDto = userService.signIn(requestDto);

        // 응답 헤더에 토큰 추가
        response.setHeader(AUTHORIZATION_HEADER, BEARER_PREFIX + jwtTokenDto.getAccessToken());
        response.setHeader(REFRESH_HEADER, jwtTokenDto.getRefreshToken());

        return ApiResp.success(HttpStatus.OK, jwtTokenDto);
    }

    @Operation(summary = "로그아웃", description = "로그아웃")
    @DeleteMapping("/v1/api/user/sign-out")
    public ApiResp<String> signOut(HttpServletRequest request) {
        userService.signOut(request);
        return ApiResp.success(HttpStatus.OK, "로그아웃 되었습니다.");
    }

    @Operation(summary = "토큰 재발급", description = "토큰 재발급")
    @PutMapping("/v1/api/user/reissue")
    public ApiResp<JwtTokenDto> reissue(HttpServletRequest request, HttpServletResponse response) {

        JwtTokenDto jwtTokenDto = userService.reissue(request);
        response.setHeader(AUTHORIZATION_HEADER, BEARER_PREFIX + jwtTokenDto.getAccessToken());
        response.setHeader(REFRESH_HEADER, jwtTokenDto.getRefreshToken());

        return ApiResp.success(HttpStatus.OK, jwtTokenDto);
    }

    @Operation(summary = "회원 좋아요", description = "회원 좋아요 누르기")
    @PostMapping("/v1/api/user/like")
    public ApiResp<String> like(HttpServletRequest request, Integer likedId){
        likeService.addLike(request,likedId);
        return ApiResp.success(HttpStatus.OK, likedId + "-> 좋아요 추가 완료");
    }

    @Operation(summary = "회원 좋아요 취소", description = "회원 좋아요 취소하기")
    @PostMapping("/v1/api/user/like-cancel")
    public ApiResp<String> cancelLike(HttpServletRequest request, Integer likedId){
        likeService.cancelLike(request,likedId);
        return ApiResp.success(HttpStatus.OK, likedId + "-> 좋아요 취소 완료");
    }


    @Operation(summary = "회원 상세 정보 보기", description = "회원 상세 정보 보기")
    @PostMapping("/v1/api/user/find/{unique-id}")
    public ApiResp<UserDetailsResponseDto> find(@PathVariable("unique-id") String uniqueId) {
        return ApiResp.success(HttpStatus.OK, userService.findByUniqueId(uniqueId));
    }

    @Operation(summary = "회원 전체 조회", description = "회원 전체 조회")
    @PostMapping("/v1/api/user/find-all")
    public ApiResp<List<UserProfileResponseDto>> findAll(HttpServletRequest request){
        return ApiResp.success(HttpStatus.OK, userService.getUserProfilesWithLikes(request));
    }



    @Operation(summary = "헤더에 토큰 확인", description = "헤더에 토큰 확인")
    @PostMapping("/token/test")
    public ApiResp<String> test(@RequestHeader HttpHeaders httpHeaders) {
        String accessToken = httpHeaders.getFirst(AUTHORIZATION_HEADER);
        String refreshToken = httpHeaders.getFirst(REFRESH_HEADER);

        // 로그 추가
        log.info("Authorization Header: {}", accessToken);
        log.info("Refresh Header: {}", refreshToken);

        if (accessToken == null || accessToken.isEmpty()) {
            return ApiResp.fail(ErrorCode.ACCESS_TOKEN_NOT_VALID);
        }
        return ApiResp.success(HttpStatus.OK,
                SecurityUtil.getCurrentUsername() + "/" + accessToken + "/" + refreshToken);
    }
}