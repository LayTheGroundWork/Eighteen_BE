package com.st.eighteen_be.jwt;

import com.st.eighteen_be.user.repository.TokenBlackList;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {

    private final TokenBlackList tokenBlackList;
    private Key key;

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.accessTokenExpireTime}")
    private long accessTokenExpireTime;

    @Value("${jwt.refreshTokenExpireTime}")
    private long refreshTokenExpireTime;

    private static final String TOKEN_TYPE = "Bearer";
    public static final String BEARER_PREFIX_A = "Bearer-";
    public static final String BEARER_PREFIX_B = "Bearer ";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String REFRESH_HEADER = "Refresh";
    private static final String AUTHORITIES_KEY = "auth";

    public JwtTokenProvider(TokenBlackList tokenBlackList) {
        this.tokenBlackList = tokenBlackList;
    }

    // 이 코드는 HMAC-SHA 키를 생성하는 데 사용되는 Base64 인코딩된 문자열을 디코딩하여 키를 초기화하는 용도로 사용
    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder()
                .decode(secretKey);// Base64로 인코딩된 값을 시크릿키 변수에 저장한 값을 디코딩하여 바이트 배열로 변환
        //* Base64 (64진법) : 바이너리(2진) 데이터를 문자 코드에 영향을 받지 않는 공통 ASCII문자로 표현하기 위해 만들어진 인코딩
        key = Keys.hmacShaKeyFor(
                bytes);//디코팅된 바이트 배열을 기반으로 HMAC-SHA 알고르즘을 사용해서 Key객체로 반환 , 이를 key 변수에 대입
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

    public long getExpiration(String accessToken) {
        Claims claims = parseClaims(accessToken);
        Date expiration = claims.getExpiration();
        long now = (new Date()).getTime();
        return expiration.getTime() - now;
    }

    // 토큰 정보를 검증하는 메서드
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith((SecretKey) key)
                    .build()
                    .parseSignedClaims(token);
            return !tokenBlackList.hasKeyBlackList(token);
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

    // Request Header에 Access Token 정보를 추출하는 메서드
    public String resolveAccessToken(String accessToken) {
        if (StringUtils.hasText(accessToken) && (accessToken.startsWith(BEARER_PREFIX_A) || accessToken.startsWith(BEARER_PREFIX_B))) {
            return accessToken.substring(7);
        }
        return null;
    }

    // Request Header에 Refresh Token 정보를 추출하는 메서드
    public String resolveRefreshToken(String refreshToken) {
        if (StringUtils.hasText(refreshToken)) {
            return refreshToken;
        }
        return null;
    }

}