package com.zhuravlov.currencyratesapi.service;

import com.zhuravlov.currencyratesapi.dto.ExchangeRatesDto;
import com.zhuravlov.currencyratesapi.infrastructure.ExchangeRatesProvider;
import com.zhuravlov.currencyratesapi.infrastructure.ExternalRatesResponse;
import com.zhuravlov.currencyratesapi.model.ExchangeRate;
import com.zhuravlov.currencyratesapi.model.ObservableCurrency;
import com.zhuravlov.currencyratesapi.repository.ExchangeRateRepository;
import com.zhuravlov.currencyratesapi.repository.ObservableCurrencyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles(value = "test")
class ExchangeRatesServiceTest {

    @Mock
    private ObservableCurrencyRepository currencyRepository;

    @Mock
    private ExchangeRateRepository rateRepository;

    @Mock
    private ExchangeRatesProvider ratesProvider;

    @InjectMocks
    private ExchangeRatesService ratesService;

    @Test
    void loadRatesFromDb() {
        String currencyBaseUSD = "USD";
        String currencyBaseEUR = "EUR";

        LocalDateTime tradedAt = LocalDateTime.now().minusHours(1);
        long tradedAtTimestamp = tradedAt.toEpochSecond(ZoneOffset.UTC);

        List<ExchangeRate> ratesUSD = List.of(
                new ExchangeRate(currencyBaseUSD, "PLN", new BigDecimal(4.05), tradedAt),
                new ExchangeRate(currencyBaseUSD, "UAH", new BigDecimal(41.75), tradedAt),
                new ExchangeRate(currencyBaseUSD, "CAD", new BigDecimal(1.25), tradedAt)
        );

        List<ExchangeRate> ratesEUR = List.of(
                new ExchangeRate(currencyBaseUSD, "PLN", new BigDecimal(4.55), tradedAt),
                new ExchangeRate(currencyBaseUSD, "UAH", new BigDecimal(45.5), tradedAt)
        );
        when(currencyRepository.findAll()).thenReturn(List.of(new ObservableCurrency(currencyBaseUSD), new ObservableCurrency(currencyBaseEUR)));
        when(rateRepository.findLatestRates(currencyBaseUSD)).thenReturn(ratesUSD);
        when(rateRepository.findLatestRates(currencyBaseEUR)).thenReturn(ratesEUR);

        ratesService.loadRatesFromDb();

        verify(rateRepository, times(2)).findLatestRates(any());

        ExchangeRatesDto ratesDtoUSD = ratesService.getExchangeRates(currencyBaseUSD);
        ExchangeRatesDto ratesDtoEUR = ratesService.getExchangeRates(currencyBaseEUR);

        assertEquals(currencyBaseUSD, ratesDtoUSD.getBase());
        assertEquals(tradedAtTimestamp, ratesDtoUSD.getTimestamp());
        assertEquals(3, ratesDtoUSD.getRates().size());
        assertEquals(new BigDecimal(41.75), ratesDtoUSD.getRates().get("UAH"));

        assertEquals(currencyBaseEUR, ratesDtoEUR.getBase());
        assertEquals(tradedAtTimestamp, ratesDtoEUR.getTimestamp());
        assertEquals(2, ratesDtoEUR.getRates().size());
        assertEquals(new BigDecimal(45.5), ratesDtoEUR.getRates().get("UAH"));
    }

    @Test
    void updateRatesAll() {
        String currencyBaseUSD = "USD";
        String currencyBaseEUR = "EUR";

        LocalDateTime tradedAt = LocalDateTime.now().minusHours(1);
        long tradedAtTimestamp = tradedAt.toEpochSecond(ZoneOffset.UTC);

        when(currencyRepository.findAll()).thenReturn(List.of(new ObservableCurrency(currencyBaseUSD), new ObservableCurrency(currencyBaseEUR)));
        when(ratesProvider.obtainExchangeRates(currencyBaseUSD)).thenReturn(
                new ExternalRatesResponse(currencyBaseUSD, Map.of(), tradedAtTimestamp));
        when(ratesProvider.obtainExchangeRates(currencyBaseEUR)).thenReturn(
                new ExternalRatesResponse(currencyBaseEUR, Map.of(), tradedAtTimestamp));

        ExchangeRatesService spyRatesService = spy(ratesService);

        spyRatesService.updateRatesAll();

        verify(ratesProvider, times(1)).obtainExchangeRates(currencyBaseUSD);
        verify(ratesProvider, times(1)).obtainExchangeRates(currencyBaseEUR);
        verify(spyRatesService, times(1)).updateRates(currencyBaseUSD);
        verify(spyRatesService, times(1)).updateRates(currencyBaseEUR);
    }

    @Test
    void updateRates() {
        String currencyBaseUSD = "USD";

        LocalDateTime tradedAt = LocalDateTime.now().minusHours(1);
        long tradedAtTimestamp = tradedAt.toEpochSecond(ZoneOffset.UTC);

        Map<String, BigDecimal> externalRatesMap = Map.of(
                "PLN", new BigDecimal(4.05),
                "UAH", new BigDecimal(41.75),
                "CAD", new BigDecimal(1.25));

        when(ratesProvider.obtainExchangeRates(currencyBaseUSD)).thenReturn(
                new ExternalRatesResponse(currencyBaseUSD, externalRatesMap, tradedAtTimestamp));

        ratesService.updateRates(currencyBaseUSD);

        verify(ratesProvider, times(1)).obtainExchangeRates(currencyBaseUSD);
        verify(rateRepository, times(1)).saveAll(any());

        ExchangeRatesDto exchangeRatesDto = ratesService.getExchangeRates(currencyBaseUSD);

        assertEquals(currencyBaseUSD, exchangeRatesDto.getBase());
        assertEquals(tradedAtTimestamp, exchangeRatesDto.getTimestamp());
        assertEquals(3, exchangeRatesDto.getRates().size());
        assertEquals(new BigDecimal(41.75), exchangeRatesDto.getRates().get("UAH"));
    }
}
