package com.st.eighteen_be.config.redis;

import com.st.eighteen_be.tournament.domain.redishash.MostLikedUserRedisHash;
import com.st.eighteen_be.user.redishash.UserSearchInfoHash;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
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
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());

        GenericToStringSerializer<Object> genericToStringSerializer = new GenericToStringSerializer<>(Object.class);
        redisTemplate.setValueSerializer(genericToStringSerializer);

        return redisTemplate;
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
    public RedisTemplate<String, MostLikedUserRedisHash> randomUserRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, MostLikedUserRedisHash> template = new RedisTemplate<>();

        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        return template;
    }
    
    @Bean
    public ReactiveRedisTemplate<String, UserSearchInfoHash> reactiveRedisTemplate(ReactiveRedisConnectionFactory connectionFactory) {
        RedisSerializationContext<String, UserSearchInfoHash> serializationContext = RedisSerializationContext
                .<String, UserSearchInfoHash>newSerializationContext(new StringRedisSerializer())
                .hashKey(new StringRedisSerializer())
                .hashValue(new GenericJackson2JsonRedisSerializer())
                .build();
        
        return new ReactiveRedisTemplate<>(connectionFactory, serializationContext);
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
