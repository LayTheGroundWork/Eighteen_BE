package com.st.eighteen_be.config.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * packageName    : com.st.eighteen_be.config.redis
 * fileName       : RedisConfig
 * author         : ipeac
 * date           : 24. 5. 10.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 5. 10.        ipeac       최초 생성
 */
@Configuration
@RequiredArgsConstructor
@EnableRedisRepositories
public class RedisConfig {
    private final RedisProperties redisProperties;

    // lettuce
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(redisProperties.getHost(), redisProperties.getPort());
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate() {
        final StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
        stringRedisTemplate.setKeySerializer(new StringRedisSerializer());
        stringRedisTemplate.setConnectionFactory(redisConnectionFactory());

        GenericToStringSerializer<Object> genericToStringSerializer = new GenericToStringSerializer<>(Object.class);
        stringRedisTemplate.setValueSerializer(genericToStringSerializer);

        return stringRedisTemplate;
    }
    
    @Bean
    public <T> RedisTemplate<String, T> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, T> template = new RedisTemplate<>();
        
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        
        return template;
    }
    
    @Bean
    public ReactiveStringRedisTemplate reactiveStringRedisTemplate(ReactiveRedisConnectionFactory connectionFactory) {
        RedisSerializationContext.RedisSerializationContextBuilder<String, String> builder =
                RedisSerializationContext.newSerializationContext(new StringRedisSerializer());
        
        // keySerializer
        builder.key(new StringRedisSerializer());
        // valueSerializer
        builder.value(new StringRedisSerializer());
        // hashKeySerializer
        builder.hashKey(new StringRedisSerializer());
        // hashValueSerializer
        builder.hashValue(new StringRedisSerializer());
        
        // 3) 빌더 build
        RedisSerializationContext<String, String> serializationContext = builder.build();
        
        // 4) ReactiveStringRedisTemplate 생성
        return new ReactiveStringRedisTemplate(connectionFactory, serializationContext);
    }
//    @PostConstruct
//    public void init() {
//        // Redis 설정 변경
//        RedisClient redisClient = RedisClient.create("redis://" + redisProperties.getHost() + ":" + redisProperties.getPort());
//        RedisCommands<String, String> commands = redisClient.connect().sync();
//        commands.configSet("stop-writes-on-bgsave-error", "no");
//        redisClient.shutdown();
//    }
}
