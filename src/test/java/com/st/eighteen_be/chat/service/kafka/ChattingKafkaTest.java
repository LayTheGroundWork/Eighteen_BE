package com.st.eighteen_be.chat.service.kafka;

import com.st.eighteen_be.chat.constant.KafkaConst;
import com.st.eighteen_be.chat.model.dto.request.ChatMessageRequestDTO;
import com.st.eighteen_be.common.annotation.ServiceWithMongoDBTest;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

@ServiceWithMongoDBTest
@Testcontainers
public class ChattingKafkaTest {
    
    @Container
    private KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.6.0")).withKraft()
            .withExposedPorts(9093);
    
    private KafkaTemplate<String, ChatMessageRequestDTO> kafkaTemplate;
    
    private Consumer<String, ChatMessageRequestDTO> consumer;
    
    private ChattingProducer chattingProducer;
    
    private ChatMessageRequestDTO messageDto;
    
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
        
        //consumer 설정
        Properties consumerProps = new Properties();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers());
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, KafkaConst.CHAT_CONSUMER_GROUP_ID);
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class.getName());
        consumerProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        
        consumer = new KafkaConsumer<>(consumerProps);
        consumer.subscribe(Collections.singleton(KafkaConst.CHAT_TOPIC));
        
        messageDto = new ChatMessageRequestDTO("1", "sender", "message", "receiver");
    }
    
    @Test
    @DisplayName("메시지 전송 성공 - 프로듀서 - 컨슈머 정상 동작 테스트")
    void When_SendMessage_With_Producer_Expect_Success() {
        // when
        chattingProducer.send(KafkaConst.CHAT_TOPIC, messageDto);
        
        // then
        ConsumerRecord<String, ChatMessageRequestDTO> record = KafkaTestUtils.getSingleRecord(consumer, KafkaConst.CHAT_TOPIC);
        assertThat(record).isNotNull();
        assertThat(record.value().roomId()).isEqualTo(messageDto.roomId());
        assertThat(record.value().sender()).isEqualTo(messageDto.sender());
        assertThat(record.value().message()).isEqualTo(messageDto.message());
        assertThat(record.value().receiver()).isEqualTo(messageDto.receiver());
    }
}