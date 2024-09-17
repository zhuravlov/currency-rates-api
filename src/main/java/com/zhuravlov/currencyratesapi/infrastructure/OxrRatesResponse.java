package com.zhuravlov.currencyratesapi.infrastructure;

import java.math.BigDecimal;
import java.util.Map;

public record OxrRatesResponse(String base, Map<String, BigDecimal> rates, long timestamp) {
}
