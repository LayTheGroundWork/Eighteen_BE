package com.st.eighteen_be.config.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * packageName    : com.st.eighteen_be.config.websocket
 * fileName       : WebSocketConfig
 * author         : ipeac
 * date           : 2024-03-29
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-03-29        ipeac       최초 생성
 */
@Slf4j
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        log.info("registerStompEndpoints : /chat, port:8080");
        
        //클라이언트가 웹소켓 서버에 연결할 엔드포인트를 정의한다.
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .setAllowedOrigins("http://localhost:8080", "http://49.142.101.231:8080");
        
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .setAllowedOrigins("http://localhost:8080", "http://49.142.101.231:8080")
                .withSockJS();
    }
    
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        log.info("configureMessageBroker : /sub, /pub");
        
        //메시지를 구독하는 주제의 접두사
        registry.enableSimpleBroker("/sub");
        
        //클라이언트가 메시지를 보내느 경우 사용하는 접두사
        registry.setApplicationDestinationPrefixes("/pub");
    }
}