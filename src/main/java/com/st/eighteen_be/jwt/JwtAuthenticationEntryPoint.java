package com.st.eighteen_be.jwt;

import com.st.eighteen_be.common.exception.ErrorCode;
import com.st.eighteen_be.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) {
        // 유효한 자격증명을 제공하지 않고 접근하려 할때 401
        ApiResponse.fail(ErrorCode.TOKEN_UNAUTHORIZED);
    }
}
