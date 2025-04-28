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
    private final MessageHandlers messageHandlers;

    public ApplicationFlows(KafkaConsumerChannelConfig kafkaConsumerChannelConfig, KafkaProducerChannelConfig kafkaProducerChannelConfig, MessageHandlers messageHandlers) {
        this.kafkaConsumerChannelConfig = kafkaConsumerChannelConfig;
        this.kafkaProducerChannelConfig = kafkaProducerChannelConfig;
        this.messageHandlers = messageHandlers;
    }


    // producer-transaction-started


    @Bean
    public IntegrationFlow incomingMessageFlow() {
        return IntegrationFlow.from(
                        Kafka.messageDrivenChannelAdapter(
                                kafkaConsumerChannelConfig.getConsumerFactory(),
                                "producer-transaction-started"
                        ).configureListenerContainer(c -> c.concurrency(10))
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
        return IntegrationFlow.from("kafkaOutboundChannel")                // İletilen mesajın geldiği kanal
                .transform(Message.class, this::modifyMessage)
                .handle(Kafka.outboundChannelAdapter(kafkaProducerChannelConfig.getProducerFactory())
                        .topic("producer-transaction-completed") // Mesajın gönderileceği output Kafka topic'i
                        .messageKeyExpression("headers['kafka_receivedMessageKey']"))  // İsteğe bağlı: Orijinal mesaj anahtarını kullanma
                .get();
    }

    private KafkaOutgoingMessage modifyMessage(Message<KafkaIncomingMessage> message) {


        KafkaIncomingMessage dto = message.getPayload();

        KafkaOutgoingMessage outgoingMessage = new KafkaOutgoingMessage();
        outgoingMessage.setMessage("Success");
        outgoingMessage.setUUID(dto.getUUID());
        return outgoingMessage;
    }

    /*





     */


}
