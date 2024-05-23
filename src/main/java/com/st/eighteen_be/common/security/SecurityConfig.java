package com.st.eighteen_be.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.st.eighteen_be.common.exception.ErrorCode;
import com.st.eighteen_be.jwt.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.encrypt.AesBytesEncryptor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

import java.io.PrintWriter;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final CorsFilter corsFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    // 대칭키
    @Value("${symmetric.key}")
    private String symmetricKey;

    @Value("${symmetric.salt}")
    private String salt;

    // 권한 확인을 하지 않는 URI
    private static final String[] PERMIT_ALL_PATTERNS = new String[] {
            "/v1/api/**",
            "/h2-console/**",
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        log.info("Security configuration initialized");
        httpSecurity
                // REST API이므로 basic auth 및 csrf 보안을 사용하지 않음
                .httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .headers(headerConfig -> headerConfig.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))

                // JWT를 사용하기 때문에 세션을 사용하지 않음
                .sessionManagement(configurer -> configurer
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(PERMIT_ALL_PATTERNS).permitAll()
                        .anyRequest().authenticated()
                )
                // JWT 인증을 위하여 직접 구현한 필터를 UsernamePasswordAuthenticationFilter 전에 실행
                .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JwtTokenAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exceptionConfig ->
                        exceptionConfig.authenticationEntryPoint(jwtAuthenticationEntryPoint)
                                .accessDeniedHandler(jwtAccessDeniedHandler)
                )
                .exceptionHandling(e ->
                        e.authenticationEntryPoint(unauthorizedEntryPoint).accessDeniedHandler(accessDeniedHandler)
                );

        return httpSecurity.build();
    }

    // AesBytesEncryptor 사용을 위한 Bean 등록
    @Bean
    public AesBytesEncryptor aesBytesEncryptor() {
        return new AesBytesEncryptor(symmetricKey, salt);
    }

    // PasswordEncoder Bean 등록
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // AuthenticationManager Bean 등록
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity httpSecurity,
                                                       PasswordEncoder passwordEncoder,
                                                       UserDetailsService userDetailsService) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                httpSecurity.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder);
        return authenticationManagerBuilder.build();
    }

    private final AuthenticationEntryPoint unauthorizedEntryPoint =
            (request, response, authException) -> {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                String json = new ObjectMapper().writeValueAsString(ErrorCode.UNAUTHORIZED);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                PrintWriter writer = response.getWriter();
                writer.write(json);
                writer.flush();
            };

    private final AccessDeniedHandler accessDeniedHandler =
            (request, response, accessDeniedException) -> {
                response.setStatus(HttpStatus.FORBIDDEN.value());
                String json = new ObjectMapper().writeValueAsString(ErrorCode.FORBIDDEN);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                PrintWriter writer = response.getWriter();
                writer.write(json);
                writer.flush();
            };
}

