package com.cengizhaner.IntegrationProducerDemo.config;

import com.cengizhaner.IntegrationProducerDemo.deserializer.CustomTcpInboundDeserializer;
import com.cengizhaner.IntegrationProducerDemo.dto.KafkaRequestDto;
import com.cengizhaner.IntegrationProducerDemo.kafka.KafkaProducerService;
import com.cengizhaner.IntegrationProducerDemo.service.TransactionStatusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.ip.tcp.TcpInboundGateway;
import org.springframework.integration.ip.tcp.connection.TcpNioServerConnectionFactory;
import org.springframework.integration.ip.tcp.serializer.ByteArrayLengthHeaderSerializer;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;

import java.util.Objects;

@Configuration
public class TcpServerConfig {

    private static final String PRODUCER_TOPIC_NAME = "producer-transaction-started";

    private static final Logger log = LoggerFactory.getLogger(TcpServerConfig.class);


    private final TransactionStatusService transactionStatusService;

    public TcpServerConfig(TransactionStatusService transactionStatusService) {
        this.transactionStatusService = transactionStatusService;
    }

    @Bean
    public TcpNioServerConnectionFactory serverConnectionFactory() {
        TcpNioServerConnectionFactory factory = new TcpNioServerConnectionFactory(3022); // port
        factory.setSerializer(new ByteArrayLengthHeaderSerializer());
        factory.setDeserializer(new CustomTcpInboundDeserializer());
        factory.setSoTimeout(10000);

        return factory;
    }

    @Bean
    public MessageChannel tcpInboundChannel() {
        return new DirectChannel();
    }

    @Bean
    public TcpInboundGateway tcpInboundGateway() {
        TcpInboundGateway gateway = new TcpInboundGateway();
        gateway.setConnectionFactory(serverConnectionFactory());
        gateway.setRequestChannel(tcpInboundChannel());
        return gateway;
    }


    @Bean
    public MessageHandler messageHandler(KafkaProducerService kafkaProducerService) {
        return new MessageHandler() {
            @Override
            public void handleMessage(Message<?> message) throws MessagingException {
                String payload = message.getPayload().toString();
                String correlationId = transactionStatusService.saveTransactionLog(payload);

                log.info("TCP Message Received. Data saved with given correlationId: {} data's flag has been set to 'W:Waiting' ", correlationId);

                KafkaRequestDto dto = new KafkaRequestDto();
                dto.setUUID(correlationId);
                dto.setData(payload);
                if (!Objects.isNull(correlationId)) {
                    try {
                        kafkaProducerService.sendMessage(PRODUCER_TOPIC_NAME, dto);
                        log.info(" Data saved with given correlationId: {} and send to '{}' topic", correlationId, PRODUCER_TOPIC_NAME);

                    } catch (Exception e) {
                        log.error("error while sending message to kafka with given entity with correlationId:{} .Error message is : {} ",
                                correlationId, e.getMessage());
                    }

                }
            }
        };
    }
}
