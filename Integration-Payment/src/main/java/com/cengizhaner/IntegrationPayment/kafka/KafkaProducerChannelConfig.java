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


    public DefaultKafkaProducerFactory<String, KafkaOutgoingMessage> getProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }



    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092"); // Kafka broker adresi
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class); // key serileştirici
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "consumer-demo-producer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class); // value serileştirici
        props.put(ProducerConfig.ACKS_CONFIG, "all"); // güvenlik için tüm replication'lar onaylasın
        return props;
    }

    @Bean
    public KafkaTemplate<String, KafkaOutgoingMessage> kafkaTemplate() {
        return new KafkaTemplate<>(getProducerFactory());
    }
}
