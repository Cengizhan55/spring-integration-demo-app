package com.cengizhaner.IntegrationProducerDemo.service;


import com.cengizhaner.IntegrationProducerDemo.entity.TransactionStatusEntity;
import com.cengizhaner.IntegrationProducerDemo.repository.TransactionStatusRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TransactionStatusService {

    private static final Logger log = LoggerFactory.getLogger(TransactionStatusService.class);

    private final TransactionStatusRepository transactionStatusRepository;


    public TransactionStatusService(TransactionStatusRepository transactionStatusRepository) {
        this.transactionStatusRepository = transactionStatusRepository;
    }

    public String saveTransactionLog(String data) {
        try {
            TransactionStatusEntity transactionStatusEntity = new TransactionStatusEntity();
            transactionStatusEntity.setData(data);
            transactionStatusEntity.setTrxConditionFlag("W");
            return transactionStatusRepository.save(transactionStatusEntity).getCorrelationId();
        } catch (Exception e) {
            log.error("error while saving db , data : " + data);
            return null;
        }


    }
}
