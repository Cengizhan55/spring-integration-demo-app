package com.cengizhaner.IntegrationPayment.dto;

public class KafkaIncomingMessage {

    public KafkaIncomingMessage() {
    }

    public KafkaIncomingMessage(String data, String UUID) {
        this.data = data;
        this.UUID = UUID;
    }

    private String data;

    private String UUID;


    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

}
