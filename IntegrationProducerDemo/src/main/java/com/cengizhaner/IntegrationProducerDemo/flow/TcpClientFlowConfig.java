package com.cengizhaner.IntegrationProducerDemo.flow;


import com.cengizhaner.IntegrationProducerDemo.dto.KafkaIncomingMessage;
import org.aopalliance.aop.Advice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.handler.advice.RequestHandlerRetryAdvice;
import org.springframework.integration.ip.tcp.TcpSendingMessageHandler;
import org.springframework.integration.ip.tcp.connection.AbstractClientConnectionFactory;
import org.springframework.integration.ip.tcp.connection.TcpNetClientConnectionFactory;
import org.springframework.integration.ip.tcp.connection.TcpNioClientConnectionFactory;
import org.springframework.integration.ip.tcp.serializer.ByteArrayCrLfSerializer;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

@Component
public class TcpClientFlowConfig {

    private final Logger logger = LoggerFactory.getLogger(TcpClientFlowConfig.class);

    private final ApplicationContext context;
    private final ApplicationEventPublisher applicationEventPublisher;

    private static final String CLIENT_IP_ADDRESS = "127.0.0.1";
    private static final Integer CLIENT_PORT = 3456;

    public TcpClientFlowConfig(ApplicationContext context, ApplicationEventPublisher applicationEventPublisher) {
        this.context = context;
        this.applicationEventPublisher = applicationEventPublisher;
    }


    @Bean
    public IntegrationFlow tcpTransformerFlow() {
        return IntegrationFlow.from("tcpOutChannel")

                .transform(Message.class, message -> {
                    KafkaIncomingMessage payload = (KafkaIncomingMessage) message.getPayload();
                    return "TXN:" + payload.getUUID(); // example tcp message
                })
                .handle(tcpOutbound(), e -> e.advice(retrySuppressingAdvice())) // 👈 Ekledik
                .get();
    }

    @Bean
    public Advice retrySuppressingAdvice() {
        return new RequestHandlerRetryAdvice() {{
            setRecoveryCallback(context -> {
                logger.warn("A TCP connection error occurred, but we are preventing Kafka from retrying.");
                return null; // swallow the exception
            });
        }};
    }

    @Bean
    public AbstractClientConnectionFactory tcpClientConnectionFactory() {
        TcpNetClientConnectionFactory factory = new TcpNetClientConnectionFactory("localhost", 3011);
        factory.setSingleUse(false); // do not close connection and use again
        factory.setSoTimeout(5000);
        factory.setApplicationEventPublisher(applicationEventPublisher);

        return factory;
    }

    @Bean
    @ServiceActivator(inputChannel = "tcpOutChannel")
    public MessageHandler tcpOutbound() {
        TcpSendingMessageHandler handler = new TcpSendingMessageHandler();
        handler.setConnectionFactory(tcpClientConnectionFactory());
        return handler;
    }


/*

    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(1); // you can change it
        scheduler.setThreadNamePrefix("tcp-MyThreadPoolTaskScheduler-");
        scheduler.initialize();
        return scheduler;
    }


    @Bean
    public TcpNioClientConnectionFactory tcpClientConnectionFactory() {
        TcpNioClientConnectionFactory factory = new TcpNioClientConnectionFactory(CLIENT_IP_ADDRESS, CLIENT_PORT);
        factory.setSerializer(byteArrayCrLfSerializer());
        //  factory.setDeserializer(new ByteArrayCrLfSerializer());
        factory.setSingleUse(false); //In every message do not open new connection open/close. in this scenario  keep open connection.
        factory.setSoKeepAlive(true);
        factory.setSoTimeout(60000); // 60.000 ms.
        factory.setApplicationContext(context);
        factory.setApplicationEventPublisher(applicationEventPublisher);
        factory.afterPropertiesSet(); // not necessary
        //factory.start();  // not necessary
        return factory;

    }


    @Bean
    public MessageHandler tcpOutbound() {
        TcpSendingMessageHandler handler = new TcpSendingMessageHandler();
        handler.setClientMode(true);
        handler.setConnectionFactory(tcpClientConnectionFactory());
        handler.setTaskScheduler(taskScheduler());
        handler.start();
        handler.onError(new Exception("Error occurred while sending message to TCP client"));
        return handler;
    }

    @Bean("byteArrayCrLfSerializer")
    public ByteArrayCrLfSerializer byteArrayCrLfSerializer() {
        return new ByteArrayCrLfSerializer();
    }

    @Bean
    public MessageChannel tcpOutChannel() {
        return new DirectChannel();
    }

    @Bean
    public IntegrationFlow tcpOutFlow() {
        return IntegrationFlow.from("tcpOutChannel") // The message comes from kafka send directly to client.
                .transform(KafkaIncomingMessage.class, KafkaIncomingMessage::getData)
                .handle(tcpOutbound())
                .get();
    }

 */
}
