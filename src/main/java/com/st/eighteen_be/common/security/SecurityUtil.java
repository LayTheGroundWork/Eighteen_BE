package com.st.eighteen_be.common.security;

import com.st.eighteen_be.common.exception.ErrorCode;
import com.st.eighteen_be.common.exception.sub_exceptions.data_exceptions.BadRequestException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class SecurityUtil {
    public static String getCurrentUsername() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new RuntimeException("No authentication information.");
        }
        return authentication.getName();
    }
    
    public static void checkUser(UserDetails userDetails) {
        if (userDetails == null) {
            throw new BadRequestException(ErrorCode.UNAUTHORIZED_TOKEN);
        }
    }
}
