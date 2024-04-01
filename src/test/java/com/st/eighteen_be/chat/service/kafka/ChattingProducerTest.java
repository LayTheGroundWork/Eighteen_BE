package com.st.eighteen_be.chat.service.kafka;

import com.st.eighteen_be.chat.model.dto.request.ChatMessageRequestDTO;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.HashMap;
import java.util.Map;

@Testcontainers
public class ChattingProducerTest {
    
    @Container
    private KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.6.0")).withKraft()
            .withExposedPorts(9093);
    
    private KafkaTemplate<String, ChatMessageRequestDTO> kafkaTemplate;
    
    private ChattingProducer chattingProducer;
    private ChatMessageRequestDTO messageDto;
    private String topic;
    
    @BeforeEach
    void setUp() {
        // 카프카 프로듀서 설정
        Map<String, Object> configs = new HashMap<>();
        configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers());
        configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        
        // ProducerFactory와 KafkaTemplate 생성
        ProducerFactory<String, ChatMessageRequestDTO> producerFactory = new DefaultKafkaProducerFactory<>(configs);
        kafkaTemplate = new KafkaTemplate<>(producerFactory);
        
        chattingProducer = new ChattingProducer(kafkaTemplate);
        topic = "chatroom-1";
        messageDto = new ChatMessageRequestDTO(1L, "sender", "message", "receiver");
    }
    
    @Test
    void When_SendMessage_Expect_Success() {
        // when
        chattingProducer.send(topic, messageDto);
        
        // then
        //컨슈머에서 메시지를 받아서 확인하는 코드
    }
}