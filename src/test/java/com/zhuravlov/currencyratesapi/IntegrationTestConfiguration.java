package com.zhuravlov.currencyratesapi;

import com.zhuravlov.currencyratesapi.infrastructure.ExchangeRatesProvider;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
class IntegrationTestConfiguration {

    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgresContainer() {
        return new PostgreSQLContainer<>(DockerImageName.parse("postgres:16"));
    }

    @Bean
    public ExchangeRatesProvider exchangeRatesProvider() {
        return new StubExchangeRatesProvider();
    }
}
