package com.zhuravlov.currencyratesapi.infrastructure;

public interface ExchangeRatesProvider {

    ExternalRatesResponse obtainExchangeRates(String baseCurrency);

}
