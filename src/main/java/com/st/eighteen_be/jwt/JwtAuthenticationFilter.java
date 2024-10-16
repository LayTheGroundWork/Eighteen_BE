package com.st.eighteen_be.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.st.eighteen_be.common.exception.ErrorCode;
import com.st.eighteen_be.common.exception.sub_exceptions.data_exceptions.jwt.CustomExpiredJwtException;
import com.st.eighteen_be.common.exception.sub_exceptions.data_exceptions.jwt.CustomIllegalArgumentException;
import com.st.eighteen_be.common.exception.sub_exceptions.data_exceptions.jwt.CustomInvalidException;
import com.st.eighteen_be.common.exception.sub_exceptions.data_exceptions.jwt.CustomUnsupportedJwtException;
import com.st.eighteen_be.common.response.ApiResp;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper = new ObjectMapper(); // 클래스 변수로 선언

    // 실제 필터링 로직은 doFilterInternal 에 들어감
    // JWT 토큰의 인증 정보를 현재 쓰레드의 SecurityContext 에 저장하는 역할 수행
    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain) throws IOException, ServletException {

        // 1. Request Header 에서 토큰을 꺼냄ㅕ
        String accessToken = request.getHeader("Authorization");
        String jwt = jwtTokenProvider.resolveAccessToken(accessToken);

        if(StringUtils.hasText(jwt)){
            // 2. validateToken 으로 토큰 유효성 검사
            // 정상 토큰이면 해당 토큰으로 Authentication 을 가져와서 SecurityContext 에 저장
            try {
                jwtTokenProvider.validateToken(jwt);
                Authentication authentication = jwtTokenProvider.getAuthentication(jwt);
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (CustomExpiredJwtException | CustomIllegalArgumentException |
                     CustomInvalidException | CustomUnsupportedJwtException e){

                setErrorResponse(response, e.getErrorCode());
                return;

            }
        }
        filterChain.doFilter(request, response);
    }

    private void setErrorResponse(HttpServletResponse response, ErrorCode errorCode){
        response.setStatus(errorCode.getStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        try{
            response.getWriter().write(objectMapper.writeValueAsString(
                    ApiResp.fail(errorCode)
            ));
        }catch (IOException e){
            log.error("An error occurred while writing the response: {}", e.getMessage());
        }
    }
}
