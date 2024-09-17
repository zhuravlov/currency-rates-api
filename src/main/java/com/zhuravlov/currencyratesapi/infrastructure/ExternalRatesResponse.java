package com.zhuravlov.currencyratesapi.infrastructure;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class ExternalRatesResponse {

    private String base;
    private Map<String, BigDecimal> rates = new HashMap<>();
    private long timestamp;

    public ExternalRatesResponse() {
    }

    public ExternalRatesResponse(String base, Map<String, BigDecimal> rates, long timestamp) {
        this.base = base;
        this.rates = rates;
        this.timestamp = timestamp;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public Map<String, BigDecimal> getRates() {
        return rates;
    }

    public void setRates(Map<String, BigDecimal> rates) {
        this.rates = rates;
    }
}
