package com.zhuravlov.currencyratesapi.dto;

import java.math.BigDecimal;
import java.util.Map;

public class ExchangeRatesDto {

    private String base;
    private Long timestamp;
    private Map<String, BigDecimal> rates;

    public ExchangeRatesDto() {
    }

    public ExchangeRatesDto(String base, long timestamp, Map<String, BigDecimal> rates) {
        this.base = base;
        this.timestamp = timestamp;
        this.rates = rates;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, BigDecimal> getRates() {
        return rates;
    }

    public void setRates(Map<String, BigDecimal> rates) {
        this.rates = rates;
    }
}
