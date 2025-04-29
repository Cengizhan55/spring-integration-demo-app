package com.cengizhaner.IntegrationProducerDemo.flow;

import com.cengizhaner.IntegrationProducerDemo.config.TcpServerConfig;
import com.cengizhaner.IntegrationProducerDemo.dto.KafkaIncomingMessage;
import com.cengizhaner.IntegrationProducerDemo.entity.TransactionStatusEntity;
import com.cengizhaner.IntegrationProducerDemo.kafka.KafkaConsumerChannelConfig;
import com.cengizhaner.IntegrationProducerDemo.kafka.KafkaProducerService;
import com.cengizhaner.IntegrationProducerDemo.repository.TransactionStatusRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.kafka.dsl.Kafka;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;

@Component

public class ApplicationFlow {

    private static final Logger log = LoggerFactory.getLogger(ApplicationFlow.class);

    private final TcpServerConfig tcpServerConfig;

    private final KafkaProducerService kafkaProducerService;

    private final KafkaConsumerChannelConfig kafkaConsumerChannelConfig;

    private final TransactionStatusRepository repository;


    public ApplicationFlow(TcpServerConfig tcpServerConfig, KafkaProducerService kafkaProducerService, KafkaConsumerChannelConfig kafkaConsumerChannelConfig, TransactionStatusRepository repository) {
        this.tcpServerConfig = tcpServerConfig;
        this.kafkaProducerService = kafkaProducerService;
        this.kafkaConsumerChannelConfig = kafkaConsumerChannelConfig;
        this.repository = repository;
    }


    @Bean
    public IntegrationFlow tcpInboundFlow() {

        return IntegrationFlow.from(tcpServerConfig.tcpInboundGateway())
                .handle(tcpServerConfig.messageHandler(kafkaProducerService))
                .get();
    }


    @Bean
    public IntegrationFlow incomingMessageFlow() {
        return IntegrationFlow.from(
                        Kafka.messageDrivenChannelAdapter(
                                kafkaConsumerChannelConfig.getConsumerFactory(),
                                "producer-transaction-completed"
                        ).configureListenerContainer(c -> c.concurrency(10))
                )
                .handle(handleKafkaResponse())
                .get();
    }


    @Bean
    MessageHandler handleKafkaResponse() {
        return new MessageHandler() {
            @Override
            public void handleMessage(Message<?> message) throws MessagingException {

                KafkaIncomingMessage dto = (KafkaIncomingMessage) message.getPayload();
                TransactionStatusEntity entity = repository.findByCorrelationId(dto.getUUID());

                entity.setTrxConditionFlag("S");
                repository.save(entity);

                log.info("Kafka Response Handle Started. With given correlationId: {} data's flag has been updated to 'S:Succesful' ", dto.getUUID());
            }
        };
    }
}
