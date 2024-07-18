package com.WorksIn.config;

import com.siot.IamportRestClient.IamportClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaymentConfig {
    @Bean
    public IamportClient iamportClient() {
        return new IamportClient("2537304135816356", "oXNZ88BiUGOQhxgnsUYOfwdun0qQT157Kv112VqVPZHbawePhWYpnAaQ7rBvoYcEjpWrwPuPCMe582gJ");
    }
}
