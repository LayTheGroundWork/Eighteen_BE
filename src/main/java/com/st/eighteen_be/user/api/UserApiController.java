package com.st.eighteen_be.user.api;

import com.st.eighteen_be.common.response.ApiResp;
import com.st.eighteen_be.jwt.JwtTokenDto;
import com.st.eighteen_be.user.dto.request.SignUpRequestDto;
import com.st.eighteen_be.user.dto.response.UserDetailsResponseDto;
import com.st.eighteen_be.user.dto.response.UserProfilePageResponseDto;
import com.st.eighteen_be.user.dto.response.UserSearchInfoResponseDto;
import com.st.eighteen_be.user.enums.CategoryType;
import com.st.eighteen_be.user.service.AuthService;
import com.st.eighteen_be.user.service.LikeService;
import com.st.eighteen_be.user.service.UserDtoService;
import com.st.eighteen_be.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

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
public class UserApiController {

    private final AuthService authService;
    private final UserDtoService userDtoService;
    private final LikeService likeService;
    private final UserService userService;

    @Operation(summary = "아이디 중복 확인", description = "아이디 중복 확인")
    @PreAuthorize("permitAll()")
    @GetMapping("/v1/api/user/duplication-check/{unique-id}")
    public ApiResp<Boolean> duplicationCheck(@PathVariable("unique-id") String uniqueId) {
        return ApiResp.success(HttpStatus.OK, authService.isDuplicationUniqueId(uniqueId));
    }

    @Operation(summary = "회원가입", description = "회원가입")
    @PostMapping("/v1/api/user/sign-up")
    public ApiResp<String> signUp(@Valid @RequestBody SignUpRequestDto requestDto,
                                  @RequestParam(value = "profileImageKeys", required = false) List<String> keys,
                                  HttpServletResponse response) {

        JwtTokenDto jwtTokenDto = authService.save(requestDto, keys);

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

        JwtTokenDto jwtTokenDto = authService.signIn(phoneNumber);

        // 응답 헤더에 토큰 추가
        response.setHeader(AUTHORIZATION_HEADER, BEARER_PREFIX_A + jwtTokenDto.getAccessToken());
        response.setHeader(REFRESH_HEADER, jwtTokenDto.getRefreshToken());

        return ApiResp.success(HttpStatus.OK, "Login Success");
    }

    @Operation(summary = "로그아웃", description = "로그아웃")
    @DeleteMapping("/v1/api/user/sign-out")
    public ApiResp<String> signOut(@RequestHeader(AUTHORIZATION_HEADER) String accessToken) {
        authService.signOut(accessToken);
        return ApiResp.success(HttpStatus.OK, "로그아웃 되었습니다.");
    }

    @Operation(summary = "토큰 재발급", description = "토큰 재발급")
    @PostMapping("/v1/api/user/reissue")
    public ApiResp<String> reissue(@RequestHeader(AUTHORIZATION_HEADER) String accessToken,
                                   @RequestHeader(REFRESH_HEADER) String refreshToken,
                                   HttpServletResponse response) {

        JwtTokenDto jwtTokenDto = authService.reissue(accessToken, refreshToken);

        response.setHeader(AUTHORIZATION_HEADER, BEARER_PREFIX_A + jwtTokenDto.getAccessToken());
        response.setHeader(REFRESH_HEADER, jwtTokenDto.getRefreshToken());

        return ApiResp.success(HttpStatus.OK, "Reissue Success");
    }

    @Operation(summary = "회원 좋아요", description = "회원 좋아요 누르기")
    @PostMapping("/v1/api/user/like")
    public ApiResp<String> like(@AuthenticationPrincipal UserDetails userDetails, @RequestParam Integer likedId) {
        userDtoService.addLike(userDetails.getUsername(), likedId);
        return ApiResp.success(HttpStatus.OK, likedId + "-> 좋아요 추가 완료");
    }

    @Operation(summary = "회원 좋아요 취소", description = "회원 좋아요 취소하기")
    @PostMapping("/v1/api/user/like-cancel")
    public ApiResp<String> cancelLike(@AuthenticationPrincipal UserDetails userDetails, @RequestParam Integer likedId) {
        userDtoService.cancelLike(userDetails.getUsername(), likedId);
        return ApiResp.success(HttpStatus.OK, likedId + "-> 좋아요 취소 완료");
    }

    @Operation(summary = "회원 상세 정보 보기", description = "회원 상세 정보 보기")
    @GetMapping("/v1/api/user/find/{unique-id}")
    public ApiResp<UserDetailsResponseDto> find(@PathVariable("unique-id") String uniqueId) {
        return ApiResp.success(HttpStatus.OK, userDtoService.findByUniqueId(uniqueId));
    }

    @Operation(summary = "[GUEST]회원 전체 조회",
            description = "순서 랜덤하게 뿌림 / 헤더에 토큰값 필수x / 페이징 처리 / request로 page랑 size만 보내주세요")
    @GetMapping("/v1/api/guest/find-all")
    public ApiResp<UserProfilePageResponseDto> findAll(@PageableDefault(page = 0, size = 10) Pageable pageable) {

        return ApiResp.success(HttpStatus.OK, userDtoService.getUserProfilePage(pageable));
    }

    @Operation(summary = "[USER]회원 전체 조회",
            description = "순서 랜덤하게 뿌림 / 헤더에 토큰값 필수 / 페이징 처리 / request로 page랑 size만 보내주세요")
    @GetMapping("/v1/api/user/find-all")
    public ApiResp<UserProfilePageResponseDto> findAll(@AuthenticationPrincipal UserDetails userDetails,
                                                       @PageableDefault(page = 0, size = 10) Pageable pageable) {

        return ApiResp.success(HttpStatus.OK, userDtoService.
                getUserProfilesWithLikes(userDetails.getUsername(), pageable));
    }

    @Operation(summary = "[GUEST] 카테고리에 맞는 회원 전체 조회",
            description = "순서 랜덤하게 뿌림 / 헤더에 토큰값 필수x / 페이징 처리 / request로 page랑 size만 보내주세요")
    @GetMapping("/v1/api/guest/find-all-by-category/{category}")
    public ApiResp<UserProfilePageResponseDto> findAllByCategory(@Parameter(description = "카테고리", required = true)
                                                                 @PathVariable("category") CategoryType category,
                                                                 @PageableDefault(page = 0, size = 10) Pageable pageable) {
        return ApiResp.success(HttpStatus.OK, userDtoService.getUserProfilesWithCategory(category, pageable));
    }

    @Operation(summary = "[USER] 카테고리에 맞는 회원 전체 조회",
            description = "순서 랜덤하게 뿌림 / 헤더에 토큰값 필수 / 페이징 처리 / request로 page랑 size만 보내주세요")
    @GetMapping("/v1/api/user/find-all-by-category/{category}")
    public ApiResp<UserProfilePageResponseDto> findAllByCategory(@AuthenticationPrincipal UserDetails userDetails,
                                                                 @PageableDefault(page = 0, size = 10) Pageable pageable,
                                                                 @Parameter(description = "카테고리", required = true)
                                                                 @PathVariable("category") CategoryType category) {
        return ApiResp.success(HttpStatus.OK, userDtoService.getUserProfilesWithLikeStatusAndCategory(
                userDetails.getUsername(), category, pageable));
    }

    @Operation(summary = "회원 탈퇴", description = "회원 탈퇴 api 수행 시 헤더에서 token 삭제해야함")
    @DeleteMapping("/v1/api/user/delete")
    public ApiResp<String> deleteUser(@AuthenticationPrincipal UserDetails userDetails) {
        return ApiResp.success(HttpStatus.OK,
                "delete Success: " + userDtoService.delete(userDetails.getUsername()));
    }

    @Operation(summary = "메인 화면 사용자(아이디/닉네임) 조회", description = "메인 화면 사용자(아이디/닉네임) 조회")
    @GetMapping("/v1/api/user/search/{search-word}")
    public Mono<ApiResp<List<UserSearchInfoResponseDto>>> searchUser(@PathVariable("search-word") String searchKey) {
        return userService.findAllUserSearchInfo(searchKey)
                       .collectList()
                       .map(dto -> ApiResp.success(HttpStatus.OK, dto));
    }

    // Test API //
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
        return ApiResp.success(HttpStatus.OK, userDtoService.findById(userId).getLikeCount());
    }
}