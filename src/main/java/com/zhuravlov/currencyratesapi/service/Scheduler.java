package com.zhuravlov.currencyratesapi.service;

import com.zhuravlov.currencyratesapi.dto.CurrencyDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Scheduler {

    private final Logger log = LoggerFactory.getLogger(Scheduler.class);

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
        log.info("Updating all observable currencies exchange rates");
        List<CurrencyDto> observableCurrencies = currencyService.getObservableCurrencies();
        log.info("Observable currencies are {}", observableCurrencies);
        for (CurrencyDto c : observableCurrencies) {
            exchangeRatesService.updateRates(c.getCode());
            log.info("{} exchange rates were updated", c.getCode());
        }
    }
}
