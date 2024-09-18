package com.zhuravlov.currencyratesapi.service;

import com.zhuravlov.currencyratesapi.dto.CurrencyDto;
import com.zhuravlov.currencyratesapi.model.ObservableCurrency;
import com.zhuravlov.currencyratesapi.repository.ObservableCurrencyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CurrencyServiceTest {

    @Mock
    private ObservableCurrencyRepository currencyRepository;

    @Mock
    private ExchangeRatesService exchangeRatesService;

    @InjectMocks
    private CurrencyService currencyService;

    @Test
    void getObservableCurrencies() {
        ObservableCurrency currencyUsd = new ObservableCurrency();
        currencyUsd.setId(1L);
        currencyUsd.setCurrencyCode("USD");

        ObservableCurrency currencyEur = new ObservableCurrency();
        currencyEur.setId(2L);
        currencyEur.setCurrencyCode("EUR");

        when(currencyRepository.findAll()).thenReturn(List.of(currencyUsd, currencyEur));

        List<CurrencyDto> observableCurrencies = currencyService.getObservableCurrencies();
        assertEquals(List.of(new CurrencyDto("USD"), new CurrencyDto("EUR")), observableCurrencies);

        verify(currencyRepository, times(1)).findAll();
    }

    @Test
    void addCurrencyNew() {
        ObservableCurrency currencyUsd = new ObservableCurrency();
        currencyUsd.setId(1L);
        currencyUsd.setCurrencyCode("USD");

        CurrencyDto currencyUsdDto = new CurrencyDto(currencyUsd.getCurrencyCode());
        when(currencyRepository.save(currencyUsd)).thenReturn(currencyUsd);

        currencyService.addCurrency(currencyUsdDto);
        verify(currencyRepository, times(1)).save(currencyUsd);
        verify(exchangeRatesService, times(1)).updateRates(currencyUsd.getCurrencyCode());
    }

    @Test
    void addCurrencyIfAlreadyExist() {
        ObservableCurrency currencyUsd = new ObservableCurrency();
        currencyUsd.setCurrencyCode("USD");

        when(currencyRepository.save(currencyUsd)).thenThrow(new RuntimeException(new DuplicateKeyException("some message")));

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> currencyService.addCurrency(new CurrencyDto(currencyUsd.getCurrencyCode()))
        );

        assertTrue(thrown.getMessage().contains("is already added"));
        verify(currencyRepository, times(1)).save(currencyUsd);
        verify(exchangeRatesService, times(0)).updateRates(currencyUsd.getCurrencyCode());
    }
}
