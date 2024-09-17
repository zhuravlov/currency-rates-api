package com.zhuravlov.currencyratesapi.service;

import com.zhuravlov.currencyratesapi.dto.CurrencyDto;
import com.zhuravlov.currencyratesapi.dto.ExchangeRatesDto;
import com.zhuravlov.currencyratesapi.infrastructure.ExternalRatesResponse;
import com.zhuravlov.currencyratesapi.infrastructure.ExchangeRatesProvider;
import com.zhuravlov.currencyratesapi.model.ExchangeRate;
import com.zhuravlov.currencyratesapi.model.ObservableCurrency;
import com.zhuravlov.currencyratesapi.repository.ExchangeRateRepository;
import com.zhuravlov.currencyratesapi.repository.ObservableCurrencyRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ExchangeRatesService {

    private final Logger log = LoggerFactory.getLogger(ExchangeRatesService.class);

    private Map<String, ExchangeRatesDto> cacheRates = new ConcurrentHashMap<>();

    private ExchangeRateRepository rateRepository;
    private ObservableCurrencyRepository currencyRepository;
    private ExchangeRatesProvider exchangeRatesProvider;

    @Autowired
    public ExchangeRatesService(ExchangeRateRepository rateRepository, ExchangeRatesProvider exchangeRatesProvider) {
        this.rateRepository = rateRepository;
        this.exchangeRatesProvider = exchangeRatesProvider;
    }

    @PostConstruct
    public void loadRates() {
        //TODO load rates from db
    }

    public ExchangeRatesDto getExchangeRates(String baseCurrency) {
        ExchangeRatesDto exchangeRatesDto = cacheRates.get(baseCurrency);
        return exchangeRatesDto;
    }

    public void updateRatesAll() {
        Collection<ObservableCurrency> all = currencyRepository.findAll();
        log.info("Observable currencies are {}", all);
        for (ObservableCurrency c : all) {
            updateRates(c.getCurrencyCode());
        }
    }

    public void updateRates(String currencyCode) {
        ExternalRatesResponse externalRatesDto = exchangeRatesProvider.obtainExchangeRates(currencyCode);

        ExchangeRatesDto ratesDto = new ExchangeRatesDto();
        ratesDto.setBase(currencyCode);
        ratesDto.setTimestamp(externalRatesDto.getTimestamp());
        ratesDto.setRates(externalRatesDto.getRates());

        cacheRates.put(currencyCode, ratesDto);

        List<ExchangeRate> exchangeRates = mapToExchangeRate(ratesDto);
        rateRepository.saveAll(exchangeRates);

        log.info("{} exchange rates were updated", currencyCode);
    }

    private List<ExchangeRate> mapToExchangeRate(ExchangeRatesDto ratesDto) {
        LocalDateTime dateTime = LocalDateTime.ofEpochSecond(ratesDto.getTimestamp(), 0, ZoneOffset.UTC);
        String base = ratesDto.getBase();
        List<ExchangeRate> result = new ArrayList<>();
        return ratesDto.getRates().entrySet()
                .stream()
                .map(e -> new ExchangeRate(base, e.getKey(), e.getValue(), dateTime))
                .toList();
    }
}
