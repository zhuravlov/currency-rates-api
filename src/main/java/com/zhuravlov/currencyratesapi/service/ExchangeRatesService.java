package com.zhuravlov.currencyratesapi.service;


import com.zhuravlov.currencyratesapi.dto.ExchangeRatesDto;
import org.springframework.stereotype.Service;

@Service
public class ExchangeRatesService {

    public ExchangeRatesDto getExchangeRates(String baseCurrency) {
        //TODO get rates from cache
        return null;
    }

    public void updateRates(String currencyCode) {
        //TODO call external api, update cache, save new rates to db
    }
}
