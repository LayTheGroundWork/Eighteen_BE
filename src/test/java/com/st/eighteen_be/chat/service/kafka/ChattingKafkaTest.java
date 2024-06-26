package com.st.eighteen_be.chat.service.kafka;

import com.st.eighteen_be.chat.constant.KafkaConst;
import com.st.eighteen_be.chat.model.collection.ChatroomInfoCollection;
import com.st.eighteen_be.chat.model.dto.request.ChatMessageRequestDTO;
import com.st.eighteen_be.chat.model.vo.ChatroomType;
import com.st.eighteen_be.chat.repository.mongo.ChatroomInfoCollectionRepository;
import com.st.eighteen_be.chat.service.ChatroomService;
import com.st.eighteen_be.chat.service.redis.RedisMessageService;
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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
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

@DisplayName("채팅 카프카 테스트")
@ServiceWithMongoDBTest
@ExtendWith(MockitoExtension.class)
@Testcontainers
public class ChattingKafkaTest {
    
    @Container
    private KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.6.0")).withKraft()
            .withExposedPorts(9093);
    
    @Autowired
    private ChatroomInfoCollectionRepository chatroomInfoCollectionRepository;
    
    @MockBean
    private RedisMessageService redisMessageService;
    
    private KafkaTemplate<String, ChatMessageRequestDTO> kafkaTemplate;
    
    private ChatroomService chatroomService;
    
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
        
        chatroomService = new ChatroomService(chatroomInfoCollectionRepository, redisMessageService);
        
        chattingProducer = new ChattingProducer(kafkaTemplate, chatroomService);
        
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
        
        messageDto = ChatMessageRequestDTO.builder()
                .senderNo(1L)
                .receiverNo(2L)
                .message("Hello")
                .build();
    }
    
    @Test
    @DisplayName("메시지 전송 성공 - 프로듀서 - 컨슈머 정상 동작 테스트")
    void When_SendMessage_With_Producer_Expect_Success() {
        //given
        //채팅방이 형성되어 있어야함.
        ChatroomInfoCollection saved = chatroomInfoCollectionRepository.save(ChatroomInfoCollection.of(1L, 2L, ChatroomType.PRIVATE));
        
        // when
        chattingProducer.send(KafkaConst.CHAT_TOPIC, messageDto, saved.get_id().toString());
        
        // then
        ConsumerRecord<String, ChatMessageRequestDTO> record = KafkaTestUtils.getSingleRecord(consumer, KafkaConst.CHAT_TOPIC);
        
        assertThat(record).isNotNull();
        assertThat(record.value().getSenderNo()).isEqualTo(messageDto.getSenderNo());
        assertThat(record.value().getMessage()).isEqualTo(messageDto.getMessage());
        assertThat(record.value().getReceiverNo()).isEqualTo(messageDto.getReceiverNo());
    }
}