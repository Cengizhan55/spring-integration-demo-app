package com.cengizhaner.IntegrationProducerDemo.config;

import com.cengizhaner.IntegrationProducerDemo.dto.KafkaRequestDto;
import com.cengizhaner.IntegrationProducerDemo.kafka.KafkaProducerService;
import com.cengizhaner.IntegrationProducerDemo.service.TransactionStatusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.ip.tcp.TcpInboundGateway;
import org.springframework.integration.ip.tcp.connection.TcpNetServerConnectionFactory;
import org.springframework.integration.ip.tcp.serializer.ByteArrayElasticRawDeserializer;
import org.springframework.integration.ip.tcp.serializer.ByteArrayLengthHeaderSerializer;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;

import java.util.Objects;

@Configuration
public class TcpServerConfig {

    private static final Logger log = LoggerFactory.getLogger(TcpServerConfig.class);


    private final TransactionStatusService transactionStatusService;

    public TcpServerConfig(TransactionStatusService transactionStatusService) {
        this.transactionStatusService = transactionStatusService;
    }

    @Bean
    public TcpNetServerConnectionFactory serverConnectionFactory() {
        TcpNetServerConnectionFactory factory = new TcpNetServerConnectionFactory(3456); // port
        factory.setSerializer(new ByteArrayLengthHeaderSerializer());
        factory.setDeserializer(new ByteArrayElasticRawDeserializer());
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

    // Gelen mesajları karşılayan handler
    @Bean
    public MessageHandler messageHandler(KafkaProducerService kafkaProducerService) {
        return new MessageHandler() {
            @Override
            public void handleMessage(Message<?> message) throws MessagingException {
                String payload = new String((byte[]) message.getPayload());
                String correlationId = transactionStatusService.saveTransactionLog(payload);
                System.out.println("TCP Message Received: " + payload);
                KafkaRequestDto dto = new KafkaRequestDto();
                dto.setUUID(correlationId);
                dto.setData(payload);
                if (!Objects.isNull(correlationId)) {
                    try {
                        kafkaProducerService.sendMessage("producer-transaction-started", dto);
                        log.info("saved to db and send to kafka");
                    } catch (Exception e) {
                        log.error("error while sending kafka ! ERROR: " + e.getMessage());
                    }

                }
            }
        };
    }
}
