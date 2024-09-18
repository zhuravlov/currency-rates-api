package com.zhuravlov.currencyratesapi;

import com.zhuravlov.currencyratesapi.infrastructure.ExchangeRatesProvider;
import com.zhuravlov.currencyratesapi.infrastructure.ExternalRatesResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

public class StubExchangeRatesProvider implements ExchangeRatesProvider {

    private final Logger log = LoggerFactory.getLogger(StubExchangeRatesProvider.class);

    private final Map<String, Map<String, BigDecimal>> ratesStubsMap = new HashMap<>() {{
        put("USA", Map.of(
                "EUR", BigDecimal.valueOf(1.1),
                "PLN", BigDecimal.valueOf(1.2),
                "UAH", BigDecimal.valueOf(1.3)));
        put("EUR", Map.of(
                "USD", BigDecimal.valueOf(2.1),
                "PLN", BigDecimal.valueOf(2.2),
                "UAH", BigDecimal.valueOf(2.3)));
        put("PLN", Map.of(
                "USD", BigDecimal.valueOf(3.1),
                "EUR", BigDecimal.valueOf(3.2),
                "UAH", BigDecimal.valueOf(3.3)));
    }};

    private final Map<String, BigDecimal> defaultRatesStub = Map.of(
            "USD", BigDecimal.valueOf(4.1),
            "EUR", BigDecimal.valueOf(4.2),
            "PLN", BigDecimal.valueOf(4.3),
            "UAH", BigDecimal.valueOf(4.4));

    public StubExchangeRatesProvider() {
        log.info("Using test stub for external rates provider");
    }

    @Override
    public ExternalRatesResponse obtainExchangeRates(String baseCurrency) {

        LocalDateTime tradedAt = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
        long tradedAtTimestamp = tradedAt.toEpochSecond(ZoneOffset.UTC);

        Map<String, BigDecimal> rates = ratesStubsMap.getOrDefault(baseCurrency, defaultRatesStub);

        return new ExternalRatesResponse(baseCurrency, rates, tradedAtTimestamp);
    }
}
