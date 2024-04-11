package com.st.eighteen_be.member.security;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.stream.Stream;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    //권한 확인을 하지 않는 uri
    private static final String[] PERMIT_ALL_PATTERNS = new String[] {

    };


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{

        http
                .csrf(AbstractHttpConfigurer::disable)
                .headers((headerConfig) -> headerConfig
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)
                )
                .authorizeHttpRequests(request ->
                        request
                                .requestMatchers(PathRequest.toH2Console())
                                .permitAll()
                                .requestMatchers( // <== 여기
                                        Stream
                                                .of(PERMIT_ALL_PATTERNS)
                                                .map(AntPathRequestMatcher::antMatcher)
                                                .toArray(AntPathRequestMatcher[]::new)
                                )
                                .permitAll()
                                .requestMatchers(AntPathRequestMatcher.antMatcher("/api/**"))
                                .authenticated()
                );


        return http.build();
    }

}
