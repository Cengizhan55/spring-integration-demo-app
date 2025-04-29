package com.cengizhaner.IntegrationProducerDemo.repository;

import com.cengizhaner.IntegrationProducerDemo.entity.TransactionStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionStatusRepository extends JpaRepository<TransactionStatusEntity, Long> {

    TransactionStatusEntity findByCorrelationId(String uuid);
}
