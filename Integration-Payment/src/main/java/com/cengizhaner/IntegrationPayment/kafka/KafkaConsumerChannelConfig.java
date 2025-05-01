package com.cengizhaner.IntegrationPayment.kafka;

import com.cengizhaner.IntegrationPayment.dto.KafkaIncomingMessage;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletionStage;

@Configuration
public class KafkaConsumerChannelConfig {

    private static final String GROUP_ID_CONSUMER_DEMO_CONSUMER = "consumer-demo-consumer";
    private static final String LOCALHOST_ADDRESS = "localhost:9092";

    public ConsumerFactory<String, KafkaIncomingMessage> getConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs());
    }


    public Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();

        // Kafka broker address
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, LOCALHOST_ADDRESS);
        // Kafka Consumer Group ID
        props.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID_CONSUMER_DEMO_CONSUMER);
        // Key deserializer
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        // Value deserializer: Deserialize the JSON data into KafkaIncomingMessageDto
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        // Specify the DTO class for JSON deserialization
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, KafkaIncomingMessage.class.getName()); // this is important
        // Disable type info headers to avoid issues like 'class not found' errors
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        // Auto offset reset: Start from the earliest message
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        // Specify trusted packages for JSON deserialization (can be restricted to specific packages)
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        props.put(ProducerConfig.ACKS_CONFIG, "all"); // approve from all replication for security

        return props;
    }


}
