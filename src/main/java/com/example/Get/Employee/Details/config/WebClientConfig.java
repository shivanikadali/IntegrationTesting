package com.example.Get.Employee.Details.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${employee.baseurl}")
    private String url;

    @Bean
    WebClient webclientBean() {
        return WebClient.builder()
                .baseUrl(url)
                .build();
    }
}
