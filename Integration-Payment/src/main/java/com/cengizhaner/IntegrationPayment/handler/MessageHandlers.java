package com.cengizhaner.IntegrationPayment.handler;

import com.cengizhaner.IntegrationPayment.dto.KafkaIncomingMessage;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Service;


@Service
public class MessageHandlers {

    @Bean
    public MessageHandler kafkaInboundMessageHandler() {

        return new MessageHandler() {
            @Override
            public void handleMessage(Message<?> message) throws MessagingException {

                KafkaIncomingMessage dto = (KafkaIncomingMessage) message.getPayload();
            //    System.out.println("-----------------------------------------------------------");
                System.out.println("-----------------------------------------------------------");
                System.out.println("Assumed that there is a business logic processed. Data uuid for processed Entity-> " + dto.getUUID());
                System.out.println("-----------------------------------------------------------");
               // System.out.println("-----------------------------------------------------------");

            }
        };
    }
}
