package com.cengizhaner.IntegrationProducerDemo.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "TRANSACTION_STATUS")
public class TransactionStatusEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "DATA")
    private String data;

    private String correlationId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Column(name = "TXN_CONDITION_FLAG")
    private String TrxConditionFlag;

    public String getTrxConditionFlag() {
        return TrxConditionFlag;
    }

    public void setTrxConditionFlag(String trxConditionFlag) {
        TrxConditionFlag = trxConditionFlag;
    }

    @PrePersist
    public void generateReferenceCode() {
        if (correlationId == null) {
            correlationId = UUID.randomUUID().toString();
        }
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }
}
