package com.st.eighteen_be.chat.service.kafka;

import com.st.eighteen_be.chat.constant.KafkaConst;
import com.st.eighteen_be.chat.model.collection.ChatMessageCollection;
import com.st.eighteen_be.chat.model.collection.ChatroomInfoCollection;
import com.st.eighteen_be.chat.model.dto.request.ChatMessageRequestDTO;
import com.st.eighteen_be.chat.repository.ChatMessageCollectionRepository;
import com.st.eighteen_be.chat.repository.ChatroomInfoCollectionRepository;
import com.st.eighteen_be.common.annotation.ServiceWithMongoDBTest;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@ServiceWithMongoDBTest
@Testcontainers
public class ChattingKafkaTest {
    
    @Container
    private KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.6.0")).withKraft()
            .withExposedPorts(9093);
    
    @Autowired
    private ChatroomInfoCollectionRepository chatroomInfoCollectionRepository;
    
    @Autowired
    private ChatMessageCollectionRepository chatMessageCollectionRepository;
    
    @MockBean
    private SimpMessagingTemplate messagingTemplate;
    
    private KafkaTemplate<String, ChatMessageRequestDTO> kafkaTemplate;
    
    private Consumer<String, ChatMessageRequestDTO> consumer;
    
    private ChattingProducer chattingProducer;
    
    private ChattingConsumer chattingConsumer;
    
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
        
        chattingConsumer = new ChattingConsumer(messagingTemplate, chatroomInfoCollectionRepository, chatMessageCollectionRepository);
        
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
    @DisplayName("메시지 전송 성공 - 컨슈머 정상 동작 테스트 - DB 정상 저장 테스트")
    void When_SendMessageUntilConsumerReceiveAndSaveMongoDB_Expect_Success() {
        // when
        chattingProducer.send(KafkaConst.CHAT_TOPIC, messageDto);
        chattingConsumer.listen(messageDto);
        
        // then
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            ChatroomInfoCollection chatroomInfo = chatroomInfoCollectionRepository.findByRoomId(messageDto.roomId()).get();
            
            ChatMessageCollection lastMessage = chatroomInfo.getChatMessageCollections().get(chatroomInfo.getChatMessageCollections().size() - 1);
            
            assertThat(lastMessage.getSender()).isEqualTo(messageDto.sender());
            assertThat(lastMessage.getMessage()).isEqualTo(messageDto.message());
            assertThat(lastMessage.getReceiver()).isEqualTo(messageDto.receiver());
        });
    }
}