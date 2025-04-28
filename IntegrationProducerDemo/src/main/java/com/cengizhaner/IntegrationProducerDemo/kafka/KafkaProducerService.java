package com.cengizhaner.IntegrationProducerDemo.kafka;

// KafkaProducerService.java

import com.cengizhaner.IntegrationProducerDemo.dto.KafkaRequestDto;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {


    private KafkaTemplate<String, KafkaRequestDto> kafkaTemplate;

    public KafkaProducerService(KafkaTemplate<String, KafkaRequestDto> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String topic, KafkaRequestDto message) {
        kafkaTemplate.send(topic, message);
    }
}
