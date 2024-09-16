package com.zhuravlov.currencyratesapi.service;

import com.zhuravlov.currencyratesapi.dto.CurrencyDto;
import com.zhuravlov.currencyratesapi.repository.ExchangeRateRepository;
import com.zhuravlov.currencyratesapi.repository.ObservableCurrencyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class CurrencyService {

    private final ObservableCurrencyRepository observableCurrencyRepository;
    private final ExchangeRateRepository exchangeRateRepository;

    @Autowired
    public CurrencyService(ObservableCurrencyRepository observableCurrencyRepository, ExchangeRateRepository exchangeRateRepository) {
        this.observableCurrencyRepository = observableCurrencyRepository;
        this.exchangeRateRepository = exchangeRateRepository;
    }

    public List<CurrencyDto> getObservableCurrencies() {
        //TODO
        return Collections.emptyList();
    }

    public CurrencyDto addCurrency(CurrencyDto currency) {
        //TODO
        return currency;
    }
}
