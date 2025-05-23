package com.cengizhaner.IntegrationPayment.flow;

import com.cengizhaner.IntegrationPayment.dto.KafkaIncomingMessage;
import com.cengizhaner.IntegrationPayment.dto.KafkaOutgoingMessage;
import com.cengizhaner.IntegrationPayment.handler.MessageHandlers;
import com.cengizhaner.IntegrationPayment.kafka.KafkaConsumerChannelConfig;
import com.cengizhaner.IntegrationPayment.kafka.KafkaProducerChannelConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.kafka.dsl.Kafka;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Service;


@Service
public class ApplicationFlows {

    private final KafkaConsumerChannelConfig kafkaConsumerChannelConfig;
    private final KafkaProducerChannelConfig kafkaProducerChannelConfig;
    private static final String TOPIC_NAME_PRODUCER_TRANSACTION_STARTED = "producer-transaction-started";
    private static final String TOPIC_NAME_PRODUCER_TRANSACTION_COMPLETED = "producer-transaction-completed";

    public ApplicationFlows(KafkaConsumerChannelConfig kafkaConsumerChannelConfig, KafkaProducerChannelConfig kafkaProducerChannelConfig) {
        this.kafkaConsumerChannelConfig = kafkaConsumerChannelConfig;
        this.kafkaProducerChannelConfig = kafkaProducerChannelConfig;

    }

    @Bean
    public IntegrationFlow incomingMessageFlow() {
        return IntegrationFlow.from(
                        Kafka.messageDrivenChannelAdapter(
                                kafkaConsumerChannelConfig.getConsumerFactory(),
                                TOPIC_NAME_PRODUCER_TRANSACTION_STARTED
                        ).configureListenerContainer(c -> c.concurrency(1))
                )
                .channel("kafkaOutboundChannel")  // İşlenen mesajı çıkış kanalına yönlendirme
                //   .handle(messageHandlers.kafkaInboundMessageHandler())
                .get();
    }

    @Bean
    MessageChannel kafkaOutboundChannel() {
        return new DirectChannel();
    }

    @Bean
    public IntegrationFlow kafkaOutboundFlow() {
        return IntegrationFlow.from("kafkaOutboundChannel")
                .transform(Message.class, this::modifyMessage)
                .handle(Kafka.outboundChannelAdapter(kafkaProducerChannelConfig.getProducerFactory())
                        .topic(TOPIC_NAME_PRODUCER_TRANSACTION_COMPLETED) // topic that message will be sent.
                        .messageKeyExpression("headers['kafka_receivedMessageKey']"))  // optional: using original message key
                .get();
    }

    private KafkaOutgoingMessage modifyMessage(Message<KafkaIncomingMessage> message) {

        KafkaIncomingMessage dto = message.getPayload();

        KafkaOutgoingMessage outgoingMessage = new KafkaOutgoingMessage();
        outgoingMessage.setMessage("Success");
        outgoingMessage.setUUID(dto.getUUID());
        return outgoingMessage;
    }
}
