package com.cengizhaner.IntegrationProducerDemo.flow;

import com.cengizhaner.IntegrationProducerDemo.config.TcpServerConfig;
import com.cengizhaner.IntegrationProducerDemo.kafka.KafkaProducerService;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.stereotype.Component;


@Component
public class TcpServerFlowConfig implements FlowConfig {


    private final TcpServerConfig tcpServerConfig;

    private final KafkaProducerService kafkaProducerService;

    public TcpServerFlowConfig(TcpServerConfig tcpServerConfig, KafkaProducerService kafkaProducerService) {
        this.tcpServerConfig = tcpServerConfig;
        this.kafkaProducerService = kafkaProducerService;
    }

    @Bean
    public IntegrationFlow tcpInboundFlow() {

        return IntegrationFlow.from(tcpServerConfig.tcpInboundGateway())
                .handle(tcpServerConfig.messageHandler(kafkaProducerService))
                .get();
    }
}
