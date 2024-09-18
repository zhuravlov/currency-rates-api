package com.zhuravlov.currencyratesapi.service;

import com.zhuravlov.currencyratesapi.dto.ExchangeRatesDto;
import com.zhuravlov.currencyratesapi.infrastructure.ExternalRatesResponse;
import com.zhuravlov.currencyratesapi.infrastructure.ExchangeRatesProvider;
import com.zhuravlov.currencyratesapi.model.ExchangeRate;
import com.zhuravlov.currencyratesapi.model.ObservableCurrency;
import com.zhuravlov.currencyratesapi.repository.ExchangeRateRepository;
import com.zhuravlov.currencyratesapi.repository.ObservableCurrencyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class ExchangeRatesService {

    private final Logger log = LoggerFactory.getLogger(ExchangeRatesService.class);

    private final Map<String, ExchangeRatesDto> cacheRates = new ConcurrentHashMap<>();

    private final ExchangeRateRepository rateRepository;
    private final ObservableCurrencyRepository currencyRepository;
    private final ExchangeRatesProvider exchangeRatesProvider;

    @Autowired
    public ExchangeRatesService(ExchangeRateRepository rateRepository, ObservableCurrencyRepository currencyRepository, ExchangeRatesProvider exchangeRatesProvider) {
        this.rateRepository = rateRepository;
        this.currencyRepository = currencyRepository;
        this.exchangeRatesProvider = exchangeRatesProvider;
    }

    @EventListener(ApplicationReadyEvent.class) // loading the cache on startup application
    public void loadRatesFromDb() {
        Collection<ObservableCurrency> all = currencyRepository.findAll();
        log.info("Loading all observable currencies rates form db: {}", all);
        for (ObservableCurrency cur : all) {
            ExchangeRatesDto exchangeRatesDto = loadFromDbLatestExchangeRatesDto(cur.getCurrencyCode());
            cacheRates.put(cur.getCurrencyCode(), exchangeRatesDto);
        }
    }

    public ExchangeRatesDto getExchangeRates(String baseCurrency) {
        ExchangeRatesDto exchangeRatesDto = cacheRates.get(baseCurrency);
        if (exchangeRatesDto == null) {
            exchangeRatesDto = loadFromDbLatestExchangeRatesDto(baseCurrency);
            cacheRates.put(baseCurrency, exchangeRatesDto);
        }
        return exchangeRatesDto;
    }

    public void updateRatesAll() {
        Collection<ObservableCurrency> all = currencyRepository.findAll();
        log.info("Updating all observable currencies rates: {}", all);
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
        return ratesDto.getRates().entrySet()
                .stream()
                .map(e -> new ExchangeRate(base, e.getKey(), e.getValue(), dateTime))
                .toList();
    }

    private ExchangeRatesDto loadFromDbLatestExchangeRatesDto(String currencyBase) {
        Collection<ExchangeRate> latestRates = rateRepository.findLatestRates(currencyBase);
        ExchangeRatesDto ratesDto = new ExchangeRatesDto();
        ratesDto.setBase(currencyBase);
        if (latestRates.isEmpty()) {
            ratesDto.setTimestamp(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
            ratesDto.setRates(Collections.emptyMap());
        } else {
            Map<String, BigDecimal> ratesDtoMap = latestRates.stream().collect(
                    Collectors.toMap(ExchangeRate::getTargetCurrencyCode, ExchangeRate::getRate));

            ratesDto.setTimestamp(latestRates.iterator().next().getTradedAt().toEpochSecond(ZoneOffset.UTC));
            ratesDto.setRates(ratesDtoMap);
        }
        return ratesDto;
    }
}
