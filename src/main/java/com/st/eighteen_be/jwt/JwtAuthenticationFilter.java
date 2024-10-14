package com.st.eighteen_be.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.st.eighteen_be.common.exception.ErrorCode;
import com.st.eighteen_be.common.exception.sub_exceptions.data_exceptions.BadRequestException;
import com.st.eighteen_be.common.exception.sub_exceptions.data_exceptions.NotValidException;
import com.st.eighteen_be.common.response.ApiResp;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;

    // 실제 필터링 로직은 doFilterInternal 에 들어감
    // JWT 토큰의 인증 정보를 현재 쓰레드의 SecurityContext 에 저장하는 역할 수행
    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain) throws IOException, ServletException {

        // 1. Request Header 에서 토큰을 꺼냄
        String accessToken = request.getHeader("Authorization");
        String jwt = jwtTokenProvider.resolveAccessToken(accessToken);


        // 2. validateToken 으로 토큰 유효성 검사
        // 정상 토큰이면 해당 토큰으로 Authentication 을 가져와서 SecurityContext 에 저장
        try {
            if (jwtTokenProvider.validateToken(jwt)) {
                Authentication authentication = jwtTokenProvider.getAuthentication(jwt);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

        } catch (BadRequestException e){
            response.getWriter().print(objectMapper.writeValueAsString(
                    ApiResp.fail(ErrorCode.EXPIRED_TOKEN)
            ));
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void setResponse(HttpServletResponse response) throws IOException {
        var responseEntity = ResponseEntity.status(ErrorCode.EXPIRED_TOKEN.getStatus()).build();
        response.getWriter().print(responseEntity);
    }

}
