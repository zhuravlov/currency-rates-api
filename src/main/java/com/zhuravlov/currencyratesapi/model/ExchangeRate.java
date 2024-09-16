package com.zhuravlov.currencyratesapi.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Table(name = "exchange_rates", schema = "public")
public class ExchangeRate {

    @Id
    private Long id;
    private String baseCurrencyCode;
    private String targetCurrencyCode;
    private BigDecimal rate;
    private LocalDateTime tradedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBaseCurrencyCode() {
        return baseCurrencyCode;
    }

    public void setBaseCurrencyCode(String baseCurrencyCode) {
        this.baseCurrencyCode = baseCurrencyCode;
    }

    public String getTargetCurrencyCode() {
        return targetCurrencyCode;
    }

    public void setTargetCurrencyCode(String targetCurrencyCode) {
        this.targetCurrencyCode = targetCurrencyCode;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public LocalDateTime getTradedAt() {
        return tradedAt;
    }

    public void setTradedAt(LocalDateTime tradedAt) {
        this.tradedAt = tradedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExchangeRate that = (ExchangeRate) o;
        return Objects.equals(getBaseCurrencyCode(), that.getBaseCurrencyCode()) && Objects.equals(getTargetCurrencyCode(), that.getTargetCurrencyCode()) && Objects.equals(getRate(), that.getRate()) && Objects.equals(getTradedAt(), that.getTradedAt());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getBaseCurrencyCode(), getTargetCurrencyCode(), getRate(), getTradedAt());
    }
}
