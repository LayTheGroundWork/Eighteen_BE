package com.st.eighteen_be.user.api;

import com.st.eighteen_be.common.exception.ErrorCode;
import com.st.eighteen_be.common.response.ApiResp;
import com.st.eighteen_be.common.security.SecurityUtil;
import com.st.eighteen_be.jwt.JwtTokenDto;
import com.st.eighteen_be.user.dto.request.SignUpRequestDto;
import com.st.eighteen_be.user.dto.response.UserDetailsResponseDto;
import com.st.eighteen_be.user.dto.response.UserProfileResponseDto;
import com.st.eighteen_be.user.service.LikeService;
import com.st.eighteen_be.user.service.UserDtoService;
import com.st.eighteen_be.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
    private final UserDtoService userDtoService;

    @Operation(summary = "아이디 중복 확인", description = "아이디 중복 확인")
    @PostMapping("/v1/api/user/duplication-check")
    public ApiResp<Boolean> duplicationCheck(@RequestParam("uniqueId") String uniqueId){
        return ApiResp.success(HttpStatus.OK, userService.isDuplicationUniqueId(uniqueId));
    }

    @Operation(summary = "회원가입", description = "회원가입")
    @PostMapping("/v1/api/user/sign-up")
    public ApiResp<String> signUp(@Valid @RequestBody SignUpRequestDto requestDto,
                                  @RequestParam("profileImageKeys") List<String> keys,
                                  HttpServletResponse response){

        JwtTokenDto jwtTokenDto = userService.save(requestDto,keys);

        // 응답 헤더에 토큰 추가
        response.setHeader(AUTHORIZATION_HEADER, BEARER_PREFIX_A + jwtTokenDto.getAccessToken());
        response.setHeader(REFRESH_HEADER, jwtTokenDto.getRefreshToken());

        return ApiResp.success(HttpStatus.OK, "Login Success");
    }

    @Operation(summary = "로그인", description = "로그인")
    @PostMapping("/v1/api/user/sign-in")
    public ApiResp<String> signIn(@Valid @RequestParam("phoneNumber")
                                           @Schema(example = "01012345679") String phoneNumber,
                                       HttpServletResponse response) {

        JwtTokenDto jwtTokenDto = userService.signIn(phoneNumber);

        // 응답 헤더에 토큰 추가
        response.setHeader(AUTHORIZATION_HEADER, BEARER_PREFIX_A + jwtTokenDto.getAccessToken());
        response.setHeader(REFRESH_HEADER, jwtTokenDto.getRefreshToken());

        return ApiResp.success(HttpStatus.OK, "Login Success");
    }

    @Operation(summary = "로그아웃", description = "로그아웃")
    @DeleteMapping("/v1/api/user/sign-out")
    public ApiResp<String> signOut(@RequestHeader(AUTHORIZATION_HEADER) String accessToken) {
        userService.signOut(accessToken);
        return ApiResp.success(HttpStatus.OK, "로그아웃 되었습니다.");
    }

    @Operation(summary = "토큰 재발급", description = "토큰 재발급")
    @PutMapping("/v1/api/user/reissue")
    public ApiResp<String> reissue(@RequestHeader(AUTHORIZATION_HEADER) String accessToken,
                                        @RequestHeader(REFRESH_HEADER) String refreshToken,
                                        HttpServletResponse response) {

        JwtTokenDto jwtTokenDto = userService.reissue(accessToken, refreshToken);

        response.setHeader(AUTHORIZATION_HEADER, BEARER_PREFIX_A + jwtTokenDto.getAccessToken());
        response.setHeader(REFRESH_HEADER, jwtTokenDto.getRefreshToken());

        return ApiResp.success(HttpStatus.OK, "Reissue Success");
    }

    @Operation(summary = "회원 좋아요", description = "회원 좋아요 누르기")
    @PostMapping("/v1/api/user/like")
    public ApiResp<String> like(@AuthenticationPrincipal UserDetails userDetails, @RequestParam Integer likedId){
        likeService.addLike(userDetails.getUsername(),likedId);
        return ApiResp.success(HttpStatus.OK, likedId + "-> 좋아요 추가 완료");
    }

    @Operation(summary = "회원 좋아요 취소", description = "회원 좋아요 취소하기")
    @PostMapping("/v1/api/user/like-cancel")
    public ApiResp<String> cancelLike(@AuthenticationPrincipal UserDetails userDetails, @RequestParam Integer likedId){
        likeService.cancelLike(userDetails.getUsername(),likedId);
        return ApiResp.success(HttpStatus.OK, likedId + "-> 좋아요 취소 완료");
    }

    @Operation(summary = "회원 상세 정보 보기", description = "회원 상세 정보 보기")
    @PostMapping("/v1/api/user/find/{unique-id}")
    public ApiResp<UserDetailsResponseDto> find(@PathVariable("unique-id") String uniqueId) {
        return ApiResp.success(HttpStatus.OK, userDtoService.findByUniqueId(uniqueId));
    }

    @Operation(summary = "[GUEST]회원 전체 조회", description = "순서 랜덤하게 뿌림 && 헤더에 토큰값 필수x")
    @PostMapping("/v1/api/guest/find-all")
    public ApiResp<List<UserProfileResponseDto>> findAll(){
        return ApiResp.success(HttpStatus.OK, userDtoService.getUserProfiles());
    }

    @Operation(summary = "[USER]회원 전체 조회", description = "순서 랜덤하게 뿌림 && 헤더에 토큰값 필수")
    @PostMapping("/v1/api/user/find-all")
    public ApiResp<List<UserProfileResponseDto>> findAll(@AuthenticationPrincipal UserDetails userDetails){
        return ApiResp.success(HttpStatus.OK, userDtoService.getUserProfilesWithLikes(userDetails.getUsername()));
    }

    @Operation(summary = "[GUEST] 카테고리에 맞는 회원 전체 조회", description = "순서 랜덤하게 뿌림 && 헤더에 토큰값 필수x")
    @PostMapping("/v1/api/guest/find-all-by-category")
    public ApiResp<List<UserProfileResponseDto>> findAllByCategory(@RequestParam("category") String category){
        return ApiResp.success(HttpStatus.OK, userDtoService.getUserProfilesWithCategory(category));
    }

    @Operation(summary = "[USER] 카테고리에 맞는 회원 전체 조회", description = "순서 랜덤하게 뿌림 && 헤더에 토큰값 필수")
    @PostMapping("/v1/api/user/find-all-by-category")
    public ApiResp<List<UserProfileResponseDto>> findAllByCategory(@AuthenticationPrincipal UserDetails userDetails,
                                                                   @RequestParam("category") String category){
        return ApiResp.success(HttpStatus.OK, userDtoService.getUserProfilesWithLikeStatusAndCategory(
                userDetails.getUsername(),category));
    }

    // Test API
    @Operation(summary = "좋아요 정보 백업 강제 시작", description = "좋아요 정보 백업 강제 시작")
    @GetMapping("/v1/api/user/like/force-start")
    public ApiResp<String> likeInfoBackupTest(){
        likeService.backupLikeCountToMySQL();
        likeService.backupUserLikeDataToMySQL();

        return ApiResp.success(HttpStatus.OK, "좋아요 정보 백업 완료");
    }

    @Operation(summary = "백업된 좋아요 정보 보기", description = "백업된 좋아요 정보 보기")
    @GetMapping("/v1/api/user/like/view-backup-data/{userId}")
    public ApiResp<Integer> viewBackupData(@PathVariable("userId") Integer userId){
        return ApiResp.success(HttpStatus.OK, userService.findById(userId).getLikeCount());
    }

    @Operation(summary = "헤더에 토큰 확인", description = "헤더에 토큰 확인")
    @GetMapping("/token/test")
    public ApiResp<String> test(@RequestHeader HttpHeaders httpHeaders) {
        String accessToken = httpHeaders.getFirst(AUTHORIZATION_HEADER);
        String refreshToken = httpHeaders.getFirst(REFRESH_HEADER);

        // 로그 추가
        log.info("Authorization Header: {}", accessToken);
        log.info("Refresh Header: {}", refreshToken);

        if (accessToken == null || accessToken.isEmpty()) {
            return ApiResp.fail(ErrorCode.INVALID_TOKEN);
        }
        return ApiResp.success(HttpStatus.OK,
                SecurityUtil.getCurrentUsername() + "/" + accessToken + "/" + refreshToken);
    }

    @Operation(summary = "회원 탈퇴", description = "회원 탈퇴 api 수행 시 헤더에서 token 삭제해야함")
    @DeleteMapping("/v1/api/user/delete")
    public ApiResp<String> deleteUser(@AuthenticationPrincipal UserDetails userDetails){
        return ApiResp.success(HttpStatus.OK,
                "delete Success: "+ userService.delete(userDetails.getUsername()));
    }
}