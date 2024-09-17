package com.zhuravlov.currencyratesapi.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Objects;
import java.util.StringJoiner;

@Table(name = "observable_currencies", schema = "public")
public class ObservableCurrency {

    @Id
    private Long id;
    private String currencyCode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ObservableCurrency that = (ObservableCurrency) o;
        return Objects.equals(currencyCode, that.currencyCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currencyCode);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", "[", "]")
                .add(currencyCode)
                .toString();
    }
}
