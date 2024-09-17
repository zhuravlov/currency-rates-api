package com.zhuravlov.currencyratesapi.service;

import com.zhuravlov.currencyratesapi.dto.ExchangeRatesDto;
import com.zhuravlov.currencyratesapi.infrastructure.ExternalRatesResponse;
import com.zhuravlov.currencyratesapi.infrastructure.ExchangeRatesProvider;
import com.zhuravlov.currencyratesapi.model.ExchangeRate;
import com.zhuravlov.currencyratesapi.repository.ExchangeRateRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ExchangeRatesService {

    private Map<String, ExchangeRatesDto> cacheRates = new ConcurrentHashMap<>();

    private ExchangeRateRepository rateRepository;
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

    public void updateRates(String currencyCode) {
        ExternalRatesResponse externalRatesDto = exchangeRatesProvider.obtainExchangeRates(currencyCode);

        ExchangeRatesDto ratesDto = new ExchangeRatesDto();
        ratesDto.setBase(currencyCode);
        ratesDto.setTimestamp(externalRatesDto.getTimestamp());
        ratesDto.setRates(externalRatesDto.getRates());

        cacheRates.put(currencyCode, new ExchangeRatesDto());

        List<ExchangeRate> exchangeRates = mapToExchangeRate(ratesDto);
        rateRepository.saveAll(exchangeRates);
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
