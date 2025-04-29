package com.cengizhaner.IntegrationPayment.kafka;

import com.cengizhaner.IntegrationPayment.dto.KafkaOutgoingMessage;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerChannelConfig {

    private static final String GROUP_ID_CONSUMER_DEMO_PRODUCER = "consumer-demo-producer";
    private static final String LOCALHOST_ADDRESS = "localhost:9092";


    public DefaultKafkaProducerFactory<String, KafkaOutgoingMessage> getProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }


    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, LOCALHOST_ADDRESS); // Kafka broker address
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class); // key serializer
        props.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID_CONSUMER_DEMO_PRODUCER);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class); // value serializer
        props.put(ProducerConfig.ACKS_CONFIG, "all"); // approve from all replication for security
        return props;
    }

    @Bean
    public KafkaTemplate<String, KafkaOutgoingMessage> kafkaTemplate() {
        return new KafkaTemplate<>(getProducerFactory());
    }
}
