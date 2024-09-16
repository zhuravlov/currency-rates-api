package com.zhuravlov.currencyratesapi.controller;

import com.zhuravlov.currencyratesapi.dto.CurrencyDto;
import com.zhuravlov.currencyratesapi.dto.ExchangeRatesDto;
import com.zhuravlov.currencyratesapi.service.CurrencyService;
import com.zhuravlov.currencyratesapi.service.ExchangeRatesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CurrencyController {

    private final Logger log = LoggerFactory.getLogger(CurrencyController.class);

    private CurrencyService currencyService;
    private ExchangeRatesService exchangeRatesService;

    @Autowired
    public CurrencyController(CurrencyService currencyService, ExchangeRatesService exchangeRatesService) {
        this.currencyService = currencyService;
        this.exchangeRatesService = exchangeRatesService;
    }

    @GetMapping("/currencies")
    public List<CurrencyDto> getCurrencies() {
        return currencyService.getObservableCurrencies();
    }

    @PostMapping("/currencies")
    public CurrencyDto addCurrency(@RequestBody CurrencyDto currency) {
        return currencyService.addCurrency(currency);
    }

    @GetMapping("/rates/{baseCurrency}")
    public ExchangeRatesDto getExchangeRates(@PathVariable String baseCurrency) {
        return exchangeRatesService.getExchangeRates(baseCurrency);
    }
}
