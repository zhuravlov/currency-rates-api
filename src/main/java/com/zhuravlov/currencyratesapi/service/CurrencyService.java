package com.zhuravlov.currencyratesapi.service;

import com.zhuravlov.currencyratesapi.dto.CurrencyDto;
import com.zhuravlov.currencyratesapi.model.ObservableCurrency;
import com.zhuravlov.currencyratesapi.repository.ObservableCurrencyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class CurrencyService {

    private final ObservableCurrencyRepository observableCurrencyRepository;
    private final ExchangeRatesService exchangeRatesService;

    @Autowired
    public CurrencyService(ObservableCurrencyRepository observableCurrencyRepository, ExchangeRatesService exchangeRatesService) {
        this.observableCurrencyRepository = observableCurrencyRepository;
        this.exchangeRatesService = exchangeRatesService;
    }

    public List<CurrencyDto> getObservableCurrencies() {
        Collection<ObservableCurrency> all = observableCurrencyRepository.findAll();
        return mapToDto(all);
    }

    public CurrencyDto addCurrency(CurrencyDto currencyDto) {
        ObservableCurrency newObservableCurrency = new ObservableCurrency();
        newObservableCurrency.setCurrencyCode(currencyDto.getCode());
        try {
            observableCurrencyRepository.save(newObservableCurrency);
        } catch (Exception e) {
            throw new RuntimeException(String.format("Currency %s is already added", currencyDto.getCode()), e);
        }
        exchangeRatesService.updateRates(currencyDto.getCode());
        return currencyDto;
    }

    private List<CurrencyDto> mapToDto(Collection<ObservableCurrency> all) {
        return all.stream().map(c -> new CurrencyDto(c.getCurrencyCode())).toList();
    }
}
