package com.st.eighteen_be.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtProvider {

    private final Key key;
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 30L;
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60L * 24 * 7;
    private static final String KEY_ROLE = "auth";

    // application-jwt.yaml에서 secret 값 가져와서 key에 저장
    public JwtProvider(@Value("${jwt.secret}") String secretKey){
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

//    public String generateAccessToken(Authentication authentication) {
//        return generateToken(authentication, ACCESS_TOKEN_EXPIRE_TIME);
//    }
//
//    // 1. refresh token 발급
//    public void generateRefreshToken(Authentication authentication, String accessToken) {
//        String refreshToken = generateToken(authentication, REFRESH_TOKEN_EXPIRE_TIME);
//        //tokenService.saveOrUpdate(authentication.getName(), refreshToken, accessToken); // redis에 저장
//    }

    // Member 정보를 가지고 AccessToken, RefreshToken을 생성하는 메서드
    public Jwt generateToken(Authentication authentication){
        // 권한 가져오기
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();

        // Access Token 생성 -> (현재시간 + 30분(밀리초))
        Date accessTokenExpiration = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);
        String accessToken = Jwts.builder()
                .subject(authentication.getName())
                .claim("auth", authorities)
                .expiration(accessTokenExpiration)
                .signWith(key)
                .compact();

        // Refresh Token 생성
        String refreshToken = Jwts.builder()
                .expiration(new Date(now + REFRESH_TOKEN_EXPIRE_TIME))
                .signWith(key)
                .compact();

        return Jwt.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    // Jwt를 복호화하여 토큰에 들어있는 정보를 꺼내는 메서드
    public Authentication getAuthentication(String accessToken){
        // Jwt 토큰 복호화
        Claims claims = parseClaims(accessToken);

        if (claims.get(KEY_ROLE) == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        // 클레임에서 권한 정보 가져오기
        Collection<? extends GrantedAuthority> authorities = getAuthorities(claims);
//                Arrays.stream(claims.get("auth").toString().split(","))
//                .map(SimpleGrantedAuthority::new)
//                .collect(Collectors.toList());

        // UserDetails 객체를 만들어서 Authentication return
        // UserDetails: interface, User: UserDetails 를 구현한 class
        User principal = new User(claims.getSubject(), "", authorities);

        // TODO: Username 말고 전화번호 또는 이메일로 하게끔
        return new UsernamePasswordAuthenticationToken(principal, accessToken, authorities);
    }

    private List<SimpleGrantedAuthority> getAuthorities(Claims claims) {
        return Collections.singletonList(new SimpleGrantedAuthority(
                claims.get(KEY_ROLE).toString()));
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

}
