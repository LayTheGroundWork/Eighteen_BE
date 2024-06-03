package com.st.eighteen_be.jwt;

import com.st.eighteen_be.common.exception.ErrorCode;
import com.st.eighteen_be.common.response.ApiResp;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) {
        // 필요한 권한이 없이 접근하려 할때 403
        ApiResp.fail(ErrorCode.TOKEN_FORBIDDEN);
    }
}
