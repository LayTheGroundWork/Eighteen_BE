package com.st.eighteen_be.jwt;

import com.st.eighteen_be.token.domain.RefreshToken;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {

    private final Key key;

    @Value("${jwt.accessTokenExpireTime}")
    private long accessTokenExpireTime;

    @Value("${jwt.refreshTokenExpireTime}")
    private long refreshTokenExpireTime;

    private static final String TOKEN_TYPE = "Bearer-";
    private static final String AUTHORITIES_KEY = "auth";

    // application-jwt.yaml에서 secret 값 가져와서 key에 저장
    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    // Member 정보를 가지고 AccessToken, RefreshToken을 생성하는 메서드
    public JwtTokenDto generateToken(Authentication authentication){

        // 권한 가져오기
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();

        // AccessToken 생성
        Date accessTokenExpiresIn = new Date(now + accessTokenExpireTime);
        String accessToken = Jwts.builder()
                .subject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .expiration(accessTokenExpiresIn)
                .signWith(key)
                .compact();

        // RefreshToken 생성
        String refreshToken = Jwts.builder()
                .expiration(new Date(now + refreshTokenExpireTime))
                .signWith(key)
                .compact();


        return JwtTokenDto.builder()
                .grantType(TOKEN_TYPE)
                .accessToken(accessToken)
                .accessTokenExpiresIn(accessTokenExpiresIn.getTime())
                .refreshToken(refreshToken)
                .build();
    }

    // Jwt를 복호화하여 토큰에 들어있는 정보를 꺼내는 메서드
    public Authentication getAuthentication(String accessToken){
        // Jwt 토큰 복호화
        Claims claims = parseClaims(accessToken);

        if (claims.get(AUTHORITIES_KEY) == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        // 클레임에서 권한 정보 가져오기
        Collection<? extends GrantedAuthority> authorities = getAuthorities(claims);

        // UserDetails 객체를 만들어서 Authentication return
        // UserDetails: interface, User: UserDetails 를 구현한 class
        UserDetails principal = new User(claims.getSubject(),"",authorities);

        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    private List<SimpleGrantedAuthority> getAuthorities(Claims claims) {
        return Collections.singletonList(new SimpleGrantedAuthority(
                claims.get(AUTHORITIES_KEY).toString()));
    }

    // 토큰 정보를 검증하는 메서드
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith((SecretKey) key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("invalid JWT", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty", e);
        }
        return false;
    }

    // accessToken
    private Claims parseClaims(String accessToken){
        try {
            return Jwts.parser()
                    .verifyWith((SecretKey) key)
                    .build()
                    .parseSignedClaims(accessToken)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
    public void setRefreshTokenAtCookie(RefreshToken refreshToken) {
        // 쿠키 생성 및 설정
        Cookie cookie = createRefreshTokenCookie(refreshToken);

        // 현재 HTTP 응답 객체 가져오기
        HttpServletResponse response = getCurrentHttpResponse();

        // 쿠키를 응답에 추가
        if (response != null) {
            response.addCookie(cookie);
        } else {
            throw new IllegalStateException("Failed to get the current HTTP response.");
        }
    }

    private Cookie createRefreshTokenCookie(RefreshToken refreshToken) {
        Cookie cookie = new Cookie("RefreshToken", refreshToken.getRefreshToken());
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setMaxAge(refreshToken.getExpiration().intValue());
        cookie.setPath("/"); // 쿠키의 경로 설정 (필요에 따라 조정 가능)
        return cookie;
    }

    private HttpServletResponse getCurrentHttpResponse() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getResponse() : null;
    }

}