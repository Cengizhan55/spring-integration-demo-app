package com.cengizhaner.IntegrationPayment.dto;

public class KafkaOutgoingMessage {



    private String UUID;

    private String message;



    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
