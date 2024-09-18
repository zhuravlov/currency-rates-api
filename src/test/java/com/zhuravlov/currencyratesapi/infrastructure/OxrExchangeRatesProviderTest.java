package com.zhuravlov.currencyratesapi.infrastructure;

import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class OxrExchangeRatesProviderTest {

    private RestTemplate restTemplate = mock(RestTemplate.class);

    private String oxrAppId = "app_id";

    private OxrExchangeRatesProvider orxProvider = new OxrExchangeRatesProvider(true, oxrAppId, restTemplate);

    @Test
    void obtainExchangeRates() {
        String currencyBaseUSD = "USD";

        LocalDateTime tradedAt = LocalDateTime.now().minusHours(1);
        long tradedAtTimestamp = tradedAt.toEpochSecond(ZoneOffset.UTC);

        Map<String, BigDecimal> externalRatesMap = Map.of(
                "PLN", new BigDecimal(4.05),
                "UAH", new BigDecimal(41.75),
                "CAD", new BigDecimal(1.25));

        OxrRatesResponse oxrBody = new OxrRatesResponse("USD", externalRatesMap, tradedAtTimestamp);
        ResponseEntity<OxrRatesResponse> restTemplateResponse = new ResponseEntity(oxrBody, HttpStatusCode.valueOf(200));

        String urlCall = "https://openexchangerates.org/api/latest.json?app_id=" + oxrAppId + "&base=" + currencyBaseUSD;

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        final HttpEntity<String> headersEntity = new HttpEntity<>(headers);

        when(restTemplate.exchange(urlCall, HttpMethod.GET, headersEntity, OxrRatesResponse.class)).thenReturn(restTemplateResponse);

        ExternalRatesResponse ratesResponse = orxProvider.obtainExchangeRates(currencyBaseUSD);

        verify(restTemplate, times(1)).exchange(urlCall, HttpMethod.GET, headersEntity, OxrRatesResponse.class);

        assertEquals(currencyBaseUSD, ratesResponse.getBase());
        assertEquals(tradedAtTimestamp, ratesResponse.getTimestamp());
        assertEquals(3, ratesResponse.getRates().size());
        assertEquals(new BigDecimal(41.75), ratesResponse.getRates().get("UAH"));
    }

    @Test
    void obtainExchangeRatesWithEmptyCurrency() {
        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> orxProvider.obtainExchangeRates("")
        );
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        final HttpEntity<String> headersEntity = new HttpEntity<>(headers);
        assertEquals("Parameter baseCurrency must be non empty", thrown.getMessage());
    }
}
