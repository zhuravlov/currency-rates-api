package com.zhuravlov.currencyratesapi;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

import java.util.TimeZone;

@Configuration
@EnableJdbcRepositories
@EnableScheduling
public class ApplicationConfig {

    @PostConstruct
    void setUTCTimeZone() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
