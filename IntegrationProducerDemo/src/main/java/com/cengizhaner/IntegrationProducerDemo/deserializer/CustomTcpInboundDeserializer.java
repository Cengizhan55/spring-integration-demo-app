package com.cengizhaner.IntegrationProducerDemo.deserializer;


import com.github.javafaker.Faker;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.core.serializer.Deserializer;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;

@Component
public class CustomTcpInboundDeserializer implements Deserializer<String> {

    private final Random random = new Random();



    @Override
    public String deserialize(InputStream inputStream) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }


        String randomStr = RandomStringUtils.randomAlphabetic(5);
        // add to end
        return sb.toString() + randomStr;
    }

    @Override
    public String deserializeFromByteArray(byte[] serialized) throws IOException {
        return Deserializer.super.deserializeFromByteArray(serialized);
    }
}
