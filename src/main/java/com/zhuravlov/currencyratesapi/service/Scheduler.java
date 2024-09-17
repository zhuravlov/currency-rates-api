package com.zhuravlov.currencyratesapi.service;

import com.zhuravlov.currencyratesapi.dto.CurrencyDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Scheduler {

    private CurrencyService currencyService;
    private ExchangeRatesService exchangeRatesService;

    @Autowired
    public Scheduler(CurrencyService currencyService, ExchangeRatesService exchangeRatesService) {
        this.currencyService = currencyService;
        this.exchangeRatesService = exchangeRatesService;
    }

    // every 1 hour
    @Scheduled(cron = "0 0 * * * *")
    public void updateRatesAll() {
        List<CurrencyDto> observableCurrencies = currencyService.getObservableCurrencies();
        for (CurrencyDto c : observableCurrencies) {
            exchangeRatesService.updateRates(c.getCode());
        }
    }
}
