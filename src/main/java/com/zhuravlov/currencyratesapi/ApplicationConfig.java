package com.zhuravlov.currencyratesapi;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

@Configuration
@EnableJdbcRepositories
@EnableScheduling
public class ApplicationConfig {

    @PostConstruct
    void setUTCTimeZone() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }
}
