package com.zhuravlov.currencyratesapi;

import org.springframework.boot.SpringApplication;

public class TestCurrencyRatesApplication {

    public static void main(String[] args) {
        SpringApplication.from(CurrencyRatesApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
