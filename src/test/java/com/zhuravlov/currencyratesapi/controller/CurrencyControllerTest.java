package com.zhuravlov.currencyratesapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhuravlov.currencyratesapi.dto.CurrencyDto;
import com.zhuravlov.currencyratesapi.dto.ExchangeRatesDto;
import com.zhuravlov.currencyratesapi.service.CurrencyService;
import com.zhuravlov.currencyratesapi.service.ExchangeRatesService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CurrencyController.class)
class CurrencyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CurrencyService currencyService;

    @MockBean
    private ExchangeRatesService exchangeRatesService;

    @Test
    void testGetAllObservableCurrencies() throws Exception {
        var usa = new CurrencyDto("USD");
        var gbr = new CurrencyDto("GBR");
        when(currencyService.getObservableCurrencies()).thenReturn(List.of(usa, gbr));

        mockMvc.perform(get("/api/currencies"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$[0].code", is("USD")))
                .andExpect(jsonPath("$[1].code", is("GBR")));

        verify(currencyService, times(1)).getObservableCurrencies();
    }

    @Test
    void testAddNewObservableCurrency() throws Exception {
        var gbr = new CurrencyDto("GBR");
        String stringJson = objectMapper.writeValueAsString(gbr);
        mockMvc.perform(post("/api/currencies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(stringJson))
                .andExpect(status().isCreated());
        verify(currencyService, times(1)).addCurrency(gbr);
    }

    @Test
    void testGetExchangeRatesForObservableCurrency() throws Exception {
        String base = "USD";
        Map<String, BigDecimal> rates = Map.of(
                "EUR", BigDecimal.valueOf(0.9D),
                "GBR", BigDecimal.valueOf(0.7D)
        );
        ExchangeRatesDto mockedRateDto = new ExchangeRatesDto(
                base, LocalDateTime.now().toEpochSecond(ZoneOffset.UTC), rates);

        when(exchangeRatesService.getExchangeRates(mockedRateDto.getBase())).thenReturn(mockedRateDto);

        // Perform the GET request
        mockMvc.perform(MockMvcRequestBuilders.get("/api/currencies/{baseCurrency}/rates", "USD"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.base", is(mockedRateDto.getBase())))
                .andExpect(jsonPath("$.timestamp", is(mockedRateDto.getTimestamp().intValue())))
                .andExpect(jsonPath("$.rates.EUR", is(mockedRateDto.getRates().get("EUR").doubleValue())))
                .andExpect(jsonPath("$.rates.GBR", is(mockedRateDto.getRates().get("GBR").doubleValue())));
        verify(exchangeRatesService, times(1)).getExchangeRates(mockedRateDto.getBase());
    }
}
