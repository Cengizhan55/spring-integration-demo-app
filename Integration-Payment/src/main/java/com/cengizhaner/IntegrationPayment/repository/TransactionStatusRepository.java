package com.cengizhaner.IntegrationPayment.repository;

import com.cengizhaner.IntegrationPayment.entity.TransactionStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//@Repository
public interface TransactionStatusRepository extends JpaRepository<TransactionStatusEntity, Long> {
   // TransactionStatusEntity findByCorrelationId(String uuid);
}
