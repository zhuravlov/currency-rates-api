package com.zhuravlov.currencyratesapi.infrastructure;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Component
public class OxrExchangeRatesProvider implements ExchangeRatesProvider {

    private static final String PROVIDER_NAME = "Open Exchange Rates";
    private static final String ORX_LATEST_RATES_BASE_URL = "https://openexchangerates.org/api/latest.json?app_id=";
    private static final String DEFAULT_BASE_CURRENCY = "USD";

    private final Logger log = LoggerFactory.getLogger(OxrExchangeRatesProvider.class);

    // Note(andrii zhuravlov): We can use only one base currency in test free plan.
    // So according task description we may use one currency as a stub for all.
    private final boolean isTestPlan;

    private final String oxrAppId;

    private final RestTemplate restTemplate;

    @Autowired
    public OxrExchangeRatesProvider(@Value("${openexchangerates.plan.test}") Boolean isTestPlan,
                                    @Value("${openexchangerates.app.id}") String oxrAppId,
                                    RestTemplate restTemplate) {
        this.isTestPlan = isTestPlan;
        this.oxrAppId = oxrAppId;
        this.restTemplate = restTemplate;
        if (isTestPlan) {
            log.warn("ORX provider is in test mode. It uses base currency for all rates.");
        }
    }

    @Override
    @Retryable(retryFor = {HttpStatusCodeException.class}, maxAttempts = 2, backoff = @Backoff(delay = 100))
    public ExternalRatesResponse obtainExchangeRates(String baseCurrency) {
        if (StringUtils.isBlank(baseCurrency)) {
            throw new RuntimeException("Parameter baseCurrency must be non empty");
        }
        String url = getUrl(baseCurrency);
        log.info("Getting {} exchange rates from external provider. Ulr: {}", baseCurrency, url);

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        final HttpEntity<String> headersEntity = new HttpEntity<>(headers);

        ResponseEntity<OxrRatesResponse> responseEntity = restTemplate.exchange(url, HttpMethod.GET, headersEntity, OxrRatesResponse.class);
        return convert(responseEntity.getBody());
    }

    private String getUrl(String baseCurrency) {
        if (isTestPlan) {
            log.warn("ORX provider is in test mode. It uses USD as base currency instead of {}", baseCurrency);
            baseCurrency = DEFAULT_BASE_CURRENCY;
        }
        return ORX_LATEST_RATES_BASE_URL + oxrAppId + "&base=" + baseCurrency;
    }

    private ExternalRatesResponse convert(OxrRatesResponse source) {
        return new ExternalRatesResponse(source.base(), source.rates(), source.timestamp());
    }
}
