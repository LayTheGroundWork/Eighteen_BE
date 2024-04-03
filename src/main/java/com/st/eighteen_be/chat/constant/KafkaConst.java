package com.st.eighteen_be.chat.constant;

/**
 * packageName    : com.st.eighteen_be.chat.constant
 * fileName       : KafkaConst
 * author         : ipeac
 * date           : 2024-04-02
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-04-02        ipeac       최초 생성
 */
public class KafkaConst {
    public static final String CHAT_TOPIC = "chatroom-";
    public static final String CHAT_CONSUMER_GROUP_ID = "eighteen-be-chat-consumer-group";
    
    public static final int KAFKA_PORT = 9092;
    
    public static final int KAFKA_TEST_PORT = 9093;
    
    public static final String KAFKA_CONTAINER_IMAGE_NAME = "confluentinc/cp-kafka:7.6.0";
}