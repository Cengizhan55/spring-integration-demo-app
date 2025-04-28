package com.cengizhaner.IntegrationPayment.handler;

import com.cengizhaner.IntegrationPayment.dto.KafkaIncomingMessage;
import com.cengizhaner.IntegrationPayment.repository.TransactionStatusRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Service;


@Service
public class MessageHandlers {


    private final TransactionStatusRepository repository;

    public MessageHandlers(TransactionStatusRepository repository) {
        this.repository = repository;
    }

    @Bean
    public MessageHandler kafkaInboundMessageHandler() {

        return new MessageHandler() {
            @Override
            public void handleMessage(Message<?> message) throws MessagingException {

                KafkaIncomingMessage dto = (KafkaIncomingMessage) message.getPayload();
                System.out.println("-----------------------------------------------------------");
                System.out.println("-----------------------------------------------------------");

                System.out.println("DATA GUID FOR THE BUSINESS PROCESS -> " + dto.getUUID());

                System.out.println("-----------------------------------------------------------");
                System.out.println("-----------------------------------------------------------");



                /*
                TransactionStatusEntity byCorrelationId = repository.findByCorrelationId(dto.getUUID());

                byCorrelationId.setTrxConditionFlag("S");
                repository.save(byCorrelationId);

                 */


            }
        };

    }
}
